"""
뉴스 RSS 크롤러
부동산 관련 뉴스를 RSS에서 수집하여 Backend API로 전송
"""

import feedparser
import requests
import yaml
import time
import logging
from datetime import datetime
from pathlib import Path
from dotenv import load_dotenv
import os
from typing import List, Dict, Optional

# ========================================
# 환경 변수 로드
# ========================================
# 1. 현재 디렉토리 .env 시도 (로컬 개발용)
load_dotenv()

# 2. backend/.env.dev 시도 (운영 환경용)
current_dir = Path(__file__).resolve().parent
backend_env_dev = current_dir.parent.parent / "backend" / ".env.dev"

if backend_env_dev.exists() and not os.getenv('BASE_URL'):
    # .env.dev 파일이 있고, 환경변수가 아직 설정 안 된 경우
    load_dotenv(backend_env_dev)
    env_source = str(backend_env_dev)
else:
    env_source = "환경변수 또는 Docker"

# ========================================
# 로깅 설정
# ========================================
logging.basicConfig(
    level=getattr(logging, os.getenv('LOG_LEVEL', 'INFO')),
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('crawler.log', encoding='utf-8'),   # ← 파일로 저장
        logging.StreamHandler()                                 # ← 콘솔로 출력
    ]
)
logger = logging.getLogger(__name__)


class NewsCrawler:
    """뉴스 RSS 크롤러 클래스"""
    
    def __init__(self, config_path: str = 'config.yaml'):
        """
        크롤러 초기화
        
        Args:
            config_path: 설정 파일 경로
        """
        # BASE_URL 사용
        base_url = os.getenv('BASE_URL', 'http://localhost:8080')

        # Docker 환경에서 host.docker.internal로 변환
        if 'localhost' in base_url or '127.0.0.1' in base_url:
            # 로컬 환경 체크
            if os.path.exists('/.dockerenv'):
                # Docker 컨테이너 내부
                self.backend_url = base_url.replace('localhost', 'host.docker.internal')
                self.backend_url = self.backend_url.replace('127.0.0.1', 'host.docker.internal')
            else:
                # 일반 환경
                self.backend_url = base_url
        else:
            # 운영 환경
            self.backend_url = base_url

        self.api_key = os.getenv('NEWS_CRAWLER_API_KEY', '')
        
        # 설정 파일 로드
        with open(config_path, 'r', encoding='utf-8') as f:
            self.config = yaml.safe_load(f)
        
        self.rss_sources = self.config['rss_sources']
        self.keywords = self.config['keywords']
        self.crawler_config = self.config['crawler']

        logger.info(f"환경변수 로드: {env_source}")
        logger.info(f"크롤러 초기화 완료 - Backend: {self.backend_url}")

    def classify_category(self, title: str, content: str) -> Optional[str]:
        """
        제목과 내용을 기반으로 카테고리 분류
        
        Args:
            title: 뉴스 제목
            content: 뉴스 내용
        
        Returns:
            분류된 카테고리 또는 None
        """
        text = (title + " " + content).lower()
        
        for category, keyword_list in self.keywords.items():
            for keyword in keyword_list:
                if keyword in text:
                    return category
        
        return None
    
    def add_prefix(self, title: str, category: str) -> str:
        """
        제목에 말머리 추가
        
        Args:
            title: 원본 제목
            category: 카테고리
        
        Returns:
            말머리가 추가된 제목
        """
        # 이미 말머리가 있는지 확인
        if title.startswith('['):
            return title
        
        return f"[{category}] {title}"
    
    def fetch_rss_feed(self, source: Dict) -> List[Dict]:
        """
        RSS 피드에서 뉴스 가져오기
        
        Args:
            source: RSS 출처 정보
        
        Returns:
            뉴스 리스트
        """
        if not source.get('enabled', True):
            logger.info(f"건너뛰기 (비활성화): {source['name']}")
            return []
        
        try:
            logger.info(f"RSS 피드 가져오기: {source['name']}")
            feed = feedparser.parse(source['url'])
            
            news_list = []
            for entry in feed.entries[:self.crawler_config['max_news_per_run']]:
                # 제목과 내용 추출
                title = entry.get('title', '').strip()
                content = entry.get('summary', entry.get('description', '')).strip()
                link = entry.get('link', '').strip()
                
                if not title or not link:
                    continue
                
                # 카테고리 분류
                category = self.classify_category(title, content)
                if not category:
                    logger.debug(f"카테고리 미분류 - 제목: {title}")
                    continue
                
                # 말머리 추가
                title_with_prefix = self.add_prefix(title, category)
                
                news_list.append({
                    'title': title_with_prefix,
                    'content': content,
                    'reference': link,
                    'source': source['name']
                })
            
            logger.info(f"{source['name']}: {len(news_list)}개 뉴스 수집")
            return news_list
            
        except Exception as e:
            logger.error(f"RSS 피드 가져오기 실패 - {source['name']}: {e}")
            return []
    
    def send_to_backend(self, news: Dict) -> bool:
        """
        Backend API로 뉴스 전송
        
        Args:
            news: 뉴스 데이터
        
        Returns:
            성공 여부
        """
        url = f"{self.backend_url}/api/news"
        headers = {
            'X-API-Key': self.api_key,
            'Content-Type': 'application/json'
        }
        
        data = {
            'title': news['title'],
            'content': news['content'],
            'reference': news['reference']
        }
        
        try:
            response = requests.post(url, json=data, headers=headers, timeout=10)
            
            if response.status_code == 201:
                logger.info(f"✅ 뉴스 등록 성공: {news['title'][:50]}...")
                return True
            elif response.status_code == 400:
                logger.warning(f"⚠️  중복 뉴스: {news['title'][:50]}...")
                return False
            elif response.status_code == 401:
                logger.error("❌ 인증 실패: API Key 확인 필요")
                return False
            else:
                logger.error(f"❌ 등록 실패 ({response.status_code}): {news['title'][:50]}...")
                return False
                
        except requests.exceptions.RequestException as e:
            logger.error(f"❌ API 요청 실패: {e}")
            return False
    
    def run(self):
        """크롤러 실행"""
        logger.info("=" * 60)
        logger.info("뉴스 크롤링 시작")
        logger.info("=" * 60)
        
        total_fetched = 0
        total_saved = 0
        
        for source in self.rss_sources:
            # RSS 피드에서 뉴스 가져오기
            news_list = self.fetch_rss_feed(source)
            total_fetched += len(news_list)
            
            # Backend로 전송
            for news in news_list:
                if self.send_to_backend(news):
                    total_saved += 1
                
                # 요청 간격
                time.sleep(self.crawler_config['request_delay'])
        
        logger.info("=" * 60)
        logger.info(f"크롤링 완료 - 수집: {total_fetched}개, 저장: {total_saved}개")
        logger.info("=" * 60)


if __name__ == '__main__':
    # 크롤러 실행
    crawler = NewsCrawler()
    crawler.run()
