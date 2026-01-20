#!/usr/bin/env python
"""
RSS 기반 뉴스 크롤러 (gRPC)
실행: python crawler.py
"""

import os
import sys
import time
import logging
import feedparser
import yaml
from pathlib import Path
from typing import Dict, List
from datetime import datetime
from dotenv import load_dotenv

# 컬러 로그 설정
try:
    import colorlog
    handler = colorlog.StreamHandler()
    handler.setFormatter(colorlog.ColoredFormatter(
        '%(log_color)s%(levelname)-8s%(reset)s %(blue)s%(message)s',
        log_colors={
            'DEBUG': 'cyan',
            'INFO': 'green',
            'WARNING': 'yellow',
            'ERROR': 'red',
            'CRITICAL': 'red,bg_white',
        }
    ))
    logger = colorlog.getLogger(__name__)
    logger.addHandler(handler)
except ImportError:
    logging.basicConfig(
        level=logging.INFO,
        format='%(levelname)-8s %(message)s'
    )
    logger = logging.getLogger(__name__)

# gRPC 의존성
try:
    import grpc
    from protos import news_pb2, news_pb2_grpc
    GRPC_AVAILABLE = True
except ImportError as e:
    GRPC_AVAILABLE = False
    logging.error(f"❌ gRPC 라이브러리 없음: {e}")
    logging.error("설치: pip install grpcio grpcio-tools")
    sys.exit(1)


class NewsCrawler:
    """RSS 뉴스 크롤러 (gRPC 전용)"""

    def __init__(self, config_path: str = 'config.yaml'):
        """크롤러 초기화"""

        # 환경 변수 로드
        self._load_env()

        # 설정 로드
        self.config = self.load_config(config_path)
        self.rss_sources = self.config.get('rss_sources', [])
        self.request_delay = self.config.get('crawler', {}).get('request_delay', 1)
        
        # 키워드는 backend에서 동적으로 가져옴
        self.keywords = {}  # {displayName: [keywords]}

        # Backend URL
        base_url = os.getenv('BASE_URL', 'http://localhost:9090')

        # Docker 환경 자동 감지
        if 'localhost' in base_url or '127.0.0.1' in base_url:
            if os.path.exists('/.dockerenv'):
                base_url = base_url.replace('localhost', 'host.docker.internal')
                base_url = base_url.replace('127.0.0.1', 'host.docker.internal')
                logger.info("🐳 Docker 환경 감지 - host.docker.internal 사용")

        self.backend_url = base_url
        self.api_key = os.getenv('NEWS_CRAWLER_API_KEY', '')

        if not self.api_key:
            logger.warning("⚠️  NEWS_CRAWLER_API_KEY 환경 변수가 설정되지 않았습니다")

        # gRPC 클라이언트 초기화
        self._init_grpc_client()
        
        # Backend에서 카테고리 정보 가져오기
        self._load_categories_from_backend()

        logger.info(f"크롤러 초기화 완료 - Backend: {self.backend_url}")
        logger.info(f"활성 RSS 출처: {len([s for s in self.rss_sources if s.get('enabled', True)])}개")
        logger.info(f"카테고리: {len(self.keywords)}개")

    def _load_env(self):
        """환경 변수 로드"""
        # 1. 로컬 .env 시도
        load_dotenv()

        # 2. backend/.env.dev 시도 (운영 환경)
        backend_env_dev = Path(__file__).parent.parent.parent / "backend" / ".env.dev"
        if backend_env_dev.exists() and not os.getenv('BASE_URL'):
            load_dotenv(backend_env_dev)
            logger.info("환경변수 로드: backend/.env.dev")
        else:
            logger.info("환경변수 로드: 환경변수 또는 Docker")

    def _init_grpc_client(self):
        """gRPC 클라이언트 초기화"""
        if not GRPC_AVAILABLE:
            logger.error("❌ gRPC 라이브러리가 설치되지 않았습니다!")
            raise ImportError("gRPC 의존성 필요: pip install grpcio grpcio-tools")

        # gRPC 주소 추출
        grpc_address = self.backend_url.replace('http://', '').replace('https://', '')

        # 채널 생성
        self.grpc_channel = grpc.insecure_channel(grpc_address)
        self.grpc_stub = news_pb2_grpc.NewsServiceStub(self.grpc_channel)

        logger.info(f"🚀 gRPC 클라이언트 초기화 완료")
        logger.info(f"gRPC 서버: {grpc_address}")

    def _load_categories_from_backend(self):
        """백엔드에서 카테고리 정보 가져오기 (공개 API)"""
        try:
            logger.info("📋 Backend에서 카테고리 정보 가져오는 중...")
            
            # gRPC 호출 (API Key 불필요 - 공개 API)
            request = news_pb2.Empty()
            response = self.grpc_stub.GetCategories(request, timeout=10.0)
            
            # 카테고리 정보를 dict로 변환
            for category_info in response.categories:
                display_name = category_info.display_name
                keywords = list(category_info.keywords)
                self.keywords[display_name] = keywords
                logger.debug(f"  - {display_name}: {len(keywords)}개 키워드")
            
            logger.info(f"✅ 카테고리 로드 성공 - {len(self.keywords)}개")
            
        except grpc.RpcError as e:
            logger.error(f"❌ [gRPC] 카테고리 로드 실패: {e.details()}")
            logger.warning("⚠️  기본 카테고리를 사용합니다 (빈 dict)")
            self.keywords = {}
        except Exception as e:
            logger.error(f"❌ 카테고리 로드 오류: {e}")
            self.keywords = {}

    def load_config(self, config_path: str) -> dict:
        """YAML 설정 파일 로드"""
        try:
            with open(config_path, 'r', encoding='utf-8') as f:
                return yaml.safe_load(f)
        except FileNotFoundError:
            logger.error(f"❌ 설정 파일을 찾을 수 없습니다: {config_path}")
            sys.exit(1)
        except yaml.YAMLError as e:
            logger.error(f"❌ YAML 파싱 오류: {e}")
            sys.exit(1)

    def classify_category(self, title: str, content: str) -> str:
        """키워드 기반 카테고리 분류"""
        text = (title + " " + content).lower()

        for category, keyword_list in self.keywords.items():
            for keyword in keyword_list:
                if keyword in text:
                    return category

        return None

    def add_prefix(self, title: str, category: str) -> str:
        """말머리 추가 (기존 말머리 제거 후)"""
        import re
        
        # 1. 기존 말머리 패턴 제거 ([한글/영문] 형식)
        # 예: [표], [속보], [Today], [부동산] 등
        title = re.sub(r'^\[[^\]]+\]\s*', '', title)
        
        # 2. 새 말머리 추가
        prefix = f"[{category}] "
        return prefix + title

    def fetch_rss_feed(self, source: dict) -> List[Dict]:
        """RSS 피드 가져오기"""
        news_list = []

        try:
            logger.info(f"RSS 피드 가져오기: {source['name']}")
            feed = feedparser.parse(source['url'])

            if not feed.entries:
                logger.warning(f"⚠️  RSS 피드가 비어있습니다: {source['name']}")
                return news_list

            # ✨ 디버그: 첫 번째 entry 구조 출력
            if feed.entries and logger.level <= logging.DEBUG:
                first_entry = feed.entries[0]
                logger.debug("=" * 60)
                logger.debug("📄 RSS Entry 구조 (1개 예시):")
                logger.debug(f"  title: {first_entry.get('title', 'N/A')[:50]}...")
                logger.debug(f"  link: {first_entry.get('link', 'N/A')[:50]}...")
                
                # description
                desc = first_entry.get('description', '')
                logger.debug(f"  description 길이: {len(desc)}자")
                logger.debug(f"  description 미리보기: {desc[:100]}...")
                
                # summary
                summ = first_entry.get('summary', '')
                logger.debug(f"  summary 길이: {len(summ)}자")
                
                # content
                content_list = first_entry.get('content', [])
                if content_list:
                    content_value = content_list[0].get('value', '')
                    content_type = content_list[0].get('type', 'N/A')
                    logger.debug(f"  content type: {content_type}")
                    logger.debug(f"  content 길이: {len(content_value)}자")
                    logger.debug(f"  content 미리보기: {content_value[:200]}...")
                    
                    # HTML 태그 포함 여부 확인
                    has_html = '<' in content_value and '>' in content_value
                    logger.debug(f"  HTML 태그 포함: {'YES ✅' if has_html else 'NO ❌'}")
                else:
                    logger.debug("  content: 없음")
                
                logger.debug("=" * 60)

            for entry in feed.entries:
                title = entry.get('title', '')
                link = entry.get('link', '')

                if not title or not link:
                    continue

                # RSS에서 content 가져오기 (SBS 같은 양질 RSS는 전체 본문 제공)
                # content:encoded (전체 HTML) > summary > description 순
                content = (
                        entry.get('content', [{}])[0].get('value', '') or
                        entry.get('summary', '') or
                        entry.get('description', '')
                )
                
                logger.debug(f"✅ RSS content 사용 ({len(content)}자)")

                # 카테고리 분류
                category = self.classify_category(title, content)

                if category:
                    # 말머리 추가
                    title_with_prefix = self.add_prefix(title, category)
                    logger.debug(f"✅ 카테고리 분류: [{category}] {title[:30]}...")

                    news_list.append({
                        'title': title_with_prefix,
                        'content': content,
                        'reference': link,
                        'category': category
                    })
                else:
                    # 카테고리를 찾지 못한 경우 스킵
                    logger.debug(f"⏭️  카테고리 미분류 (스킵): {title[:50]}...")

            logger.info(f"✅ 수집 완료: {len(news_list)}개 (카테고리 매칭됨)")

        except Exception as e:
            logger.error(f"❌ RSS 피드 가져오기 실패 [{source['name']}]: {e}")

        return news_list

    def send_to_backend(self, news: Dict) -> bool:
        """gRPC로 뉴스 전송"""
        try:
            # 메타데이터 (API Key)
            metadata = (('x-api-key', self.api_key),)

            # 요청 생성 (category 포함)
            request = news_pb2.NewsCreateRequest(
                title=news['title'],
                content=news['content'],
                reference=news['reference'],
                category=news.get('category', '')
            )

            # gRPC 호출
            response = self.grpc_stub.CreateNews(
                request,
                metadata=metadata,
                timeout=10.0
            )

            logger.info(f"✅ [gRPC] 뉴스 등록 성공 - ID: {response.id}, 제목: {news['title'][:50]}...")
            return True

        except grpc.RpcError as e:
            if e.code() == grpc.StatusCode.ALREADY_EXISTS:
                logger.warning(f"⚠️  [gRPC] 중복 뉴스: {news['title'][:50]}...")
                return False
            elif e.code() == grpc.StatusCode.UNAUTHENTICATED:
                logger.error(f"❌ [gRPC] 인증 실패 - API Key 확인 필요")
                return False
            elif e.code() == grpc.StatusCode.INVALID_ARGUMENT:
                logger.error(f"❌ [gRPC] 요청 검증 실패: {e.details()}")
                return False
            else:
                logger.error(f"❌ [gRPC] gRPC 오류 [{e.code()}]: {e.details()}")
                return False
        except Exception as e:
            logger.error(f"❌ [gRPC] 예상치 못한 오류: {e}")
            return False

    def run(self):
        """크롤링 실행"""
        logger.info("=" * 60)
        logger.info("뉴스 크롤링 시작")
        logger.info("=" * 60)

        total_collected = 0
        total_saved = 0

        for source in self.rss_sources:
            # 비활성화된 출처 스킵
            if not source.get('enabled', True):
                logger.info(f"⏭️  스킵: {source['name']} (비활성화)")
                continue

            # RSS 피드 가져오기
            news_list = self.fetch_rss_feed(source)
            total_collected += len(news_list)

            # Backend로 전송
            for news in news_list:
                if self.send_to_backend(news):
                    total_saved += 1

        logger.info("=" * 60)
        logger.info(f"크롤링 완료 - 수집: {total_collected}개, 저장: {total_saved}개")
        logger.info("=" * 60)

    def __del__(self):
        """채널 정리"""
        if hasattr(self, 'grpc_channel'):
            self.grpc_channel.close()


if __name__ == "__main__":
    # 로그 레벨 설정
    log_level = os.getenv('LOG_LEVEL', 'INFO').upper()
    logger.setLevel(getattr(logging, log_level, logging.INFO))

    try:
        crawler = NewsCrawler()
        crawler.run()
    except KeyboardInterrupt:
        logger.info("\n\n⏹️  크롤링 중단됨")
        sys.exit(0)
    except Exception as e:
        logger.error(f"❌ 크롤러 실행 오류: {e}")
        sys.exit(1)