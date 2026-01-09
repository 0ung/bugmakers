"""
뉴스 크롤러 스케줄러
주기적으로 뉴스를 자동 수집
"""

import schedule
import time
import logging
from crawler import NewsCrawler
from datetime import datetime

# 로깅 설정
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s',
    handlers=[
        logging.FileHandler('scheduler.log', encoding='utf-8'),
        logging.StreamHandler()
    ]
)
logger = logging.getLogger(__name__)


def job():
    """스케줄 작업"""
    try:
        logger.info("🔄 스케줄 작업 시작")
        crawler = NewsCrawler()
        crawler.run()
        logger.info("✅ 스케줄 작업 완료\n")
    except Exception as e:
        logger.error(f"❌ 스케줄 작업 실패: {e}\n")


if __name__ == '__main__':
    logger.info("=" * 60)
    logger.info("뉴스 크롤러 스케줄러 시작")
    logger.info("=" * 60)
    
    # 스케줄 설정
    # 매일 오전 9시, 오후 3시, 오후 9시 실행
    schedule.every().day.at("09:00").do(job)
    schedule.every().day.at("15:00").do(job)
    schedule.every().day.at("21:00").do(job)
    
    logger.info("📅 스케줄 등록:")
    logger.info("  - 매일 오전 09:00")
    logger.info("  - 매일 오후 15:00")
    logger.info("  - 매일 오후 21:00")
    logger.info("")
    
    # 시작 시 한 번 실행
    logger.info("🚀 초기 크롤링 실행")
    job()
    
    # 스케줄 루프
    while True:
        schedule.run_pending()
        time.sleep(60)  # 1분마다 체크
