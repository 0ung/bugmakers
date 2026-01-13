FROM python:3.11-slim

WORKDIR /app

# 의존성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 소스 코드 복사
COPY . .

# 환경 변수 (기본값공란, docker-compose에서 오버라이드)
ENV BACKEND_URL=""
ENV NEWS_CRAWLER_API_KEY=""
ENV LOG_LEVEL=INFO

# 스케줄러 실행
CMD ["python", "crawler.py"]
