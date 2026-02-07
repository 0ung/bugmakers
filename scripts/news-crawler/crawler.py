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
import json
import re
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

        # gRPC Server URL (Docker 네트워크 내부 통신)
        self.backend_url = os.getenv('GRPC_SERVER', 'localhost:50051')
        
        logger.info(f"🔗 gRPC 서버: {self.backend_url}")
        self.api_key = os.getenv('NEWS_CRAWLER_API_KEY', '')

        if not self.api_key:
            logger.warning("⚠️  NEWS_CRAWLER_API_KEY 환경 변수가 설정되지 않았습니다")

        # gRPC 클라이언트 초기화
        self._init_grpc_client()

        logger.info(f"크롤러 초기화 완료")
        logger.info(f"활성 RSS 출처: {len([s for s in self.rss_sources if s.get('enabled', True)])}개")

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
        """
        Backend API를 호출하여 카테고리 분류 (가중치 기반)
        
        Returns:
            카테고리 displayName (예: "부동산", "정책", "일반")
            "일반"이 반환되면 부동산 관련 뉴스가 아님
        """
        try:
            # gRPC ClassifyCategory API 호출
            logger.info(f"🔍 카테고리 분류 요청: {title[:50]}...")
            
            # 메타데이터 (API Key) - ClassifyCategory도 인증 필요
            metadata = (('x-api-key', self.api_key),)
            
            request = news_pb2.ClassifyRequest(
                title=title,
                content=content[:500]  # 콘텐츠는 첫 500자만 전송
            )
            
            response = self.grpc_stub.ClassifyCategory(
                request, 
                metadata=metadata,  # API Key 전달
                timeout=5.0
            )
            category = response.category
            
            # INFO 레벨에서도 분류 결과 표시
            logger.info(f"🎯 분류 결과: [{category}] {title[:50]}...")
            return category
            
        except grpc.RpcError as e:
            logger.error(f"❌ [gRPC] 카테고리 분류 실패: {e.code()} - {e.details()}")
            logger.error(f"  ❌ 제목: {title[:50]}...")
            return "일반"  # 실패 시 "일반"으로 처리 (스킵됨)
        except Exception as e:
            logger.error(f"❌ 카테고리 분류 오류: {e}")
            logger.error(f"  ❌ 제목: {title[:50]}...")
            return "일반"

    def add_prefix(self, title: str, category: str) -> str:
        """말머리 추가 (기존 말머리 제거 후)"""
        # 1. 기존 말머리 패턴 제거 ([한글/영문] 형식)
        # 예: [표], [속보], [Today], [부동산] 등
        title = re.sub(r'^\[[^\]]+\]\s*', '', title)
        
        # 2. 새 말머리 추가
        prefix = f"[{category}] "
        return prefix + title

    def extract_images(self, html_content: str) -> tuple:
        """
        HTML 콘텐츠에서 이미지 URL 추출 (개선 버전)
        
        Args:
            html_content: HTML 문자열
            
        Returns:
            (thumbnail_url, detail_image_url)
            - 이미지 0개: (None, None)
            - 이미지 1개: (img[0], img[0])  # 같은 이미지 사용
            - 이미지 2+개: (img[0], img[1])
        """
        try:
            # <img> 태그에서 src 추출 (개선된 정규식)
            img_pattern = r'<img[^>]+src=["\'](https?://[^"\']+)["\']'
            image_urls = re.findall(img_pattern, html_content, re.IGNORECASE)
            
            # 디버깅: 추출된 이미지 URL 모두 출력
            if image_urls:
                logger.debug(f"🖼️  추출된 이미지 URL ({len(image_urls)}개):")
                for idx, url in enumerate(image_urls[:5], 1):  # 처음 5개만
                    logger.debug(f"  [{idx}] {url[:80]}..." if len(url) > 80 else f"  [{idx}] {url}")
            
            # 중복 제거 & 필터링
            unique_images = []
            for url in image_urls:
                # HTML entity 디코딩 (&amp; -> &)
                url = url.replace('&amp;', '&')
                
                # 유효한 이미지 URL만 포함
                if (url.startswith('http') and 
                    url not in unique_images and
                    not url.startswith('data:') and
                    not any(ext in url.lower() for ext in ['.jsp', '.do', '.php', '.asp', 'tracking', 'redirect'])):
                    unique_images.append(url)
            
            # 이미지 개수에 따른 처리
            if len(unique_images) == 0:
                logger.debug("🖼️  이미지 없음 (Frontend 기본 이미지 사용)")
                return None, None
                
            elif len(unique_images) == 1:
                logger.debug(f"🖼️  이미지 1개 (썸네일=상세): {unique_images[0][:60]}...")
                return unique_images[0], unique_images[0]
                
            else:
                logger.debug(f"🖼️  이미지 {len(unique_images)}개 (썸네일=1번째, 상세=2번째)")
                logger.debug(f"  썸네일: {unique_images[0][:60]}...")
                logger.debug(f"  상세: {unique_images[1][:60]}...")
                return unique_images[0], unique_images[1]
            
        except Exception as e:
            logger.warning(f"⚠️  이미지 추출 오류: {e}")
            return None, None

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
                
                # content
                content_list = first_entry.get('content', [])
                if content_list:
                    content_value = content_list[0].get('value', '')
                    logger.debug(f"  content 길이: {len(content_value)}자")
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

                # RSS에서 content 가져오기
                content = (
                        entry.get('content', [{}])[0].get('value', '') or
                        entry.get('summary', '') or
                        entry.get('description', '')
                )
                
                logger.debug(f"✅ RSS content 사용 ({len(content)}자)")

                # 이미지 추출
                thumbnail_url, detail_image_url = self.extract_images(content)

                # 🎯 Backend API를 통한 카테고리 분류 (가중치 기반)
                category = self.classify_category(title, content)

                # 🚨 "일반" 카테고리는 스킵 (부동산 관련 뉴스가 아님)
                if category == "일반":
                    logger.info(f"⏭️  [일반] 부동산 무관 뉴스 스킵: {title[:50]}...")
                    continue

                # 말머리 추가
                title_with_prefix = self.add_prefix(title, category)
                logger.debug(f"✅ 카테고리 분류: [{category}] {title[:30]}...")

                news_list.append({
                    'title': title_with_prefix,
                    'content': content,
                    'reference': link,
                    'category': category,
                    'thumbnail_url': thumbnail_url,
                    'detail_image_url': detail_image_url
                })

            logger.info(f"✅ 수집 완료: {len(news_list)}개 (부동산 관련 뉴스만)")

        except Exception as e:
            logger.error(f"❌ RSS 피드 가져오기 실패 [{source['name']}]: {e}")

        return news_list

    def send_to_backend(self, news: Dict) -> bool:
        """gRPC로 뉴스 전송"""
        try:
            # 메타데이터 (API Key)
            metadata = (('x-api-key', self.api_key),)

            # 요청 생성
            request = news_pb2.NewsCreateRequest(
                title=news['title'],
                content=news['content'],
                reference=news['reference'],
                category=news.get('category', ''),
                thumbnail_url=news.get('thumbnail_url', '') or '',
                detail_image_url=news.get('detail_image_url', '') or ''
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
        logger.info("🏠 부동산 뉴스 크롤링 시작")
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
        logger.info(f"✅ 크롤링 완료 - 부동산 관련 뉴스: {total_collected}개, 저장: {total_saved}개")
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
