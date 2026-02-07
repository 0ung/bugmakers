FROM python:3.11-slim

WORKDIR /app

# 타임존 설정 (한국 시간)
ENV TZ=Asia/Seoul
RUN apt-get update && \
    apt-get install -y cron tzdata && \
    ln -snf /usr/share/zoneinfo/$TZ /etc/localtime && \
    echo $TZ > /etc/timezone && \
    rm -rf /var/lib/apt/lists/*

# 의존성 파일 복사 및 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 소스 코드 복사
COPY . .

# 환경 변수
ENV GRPC_SERVER=""
ENV NEWS_CRAWLER_API_KEY=""
ENV LOG_LEVEL=INFO

# cron 작업 설정 (매일 00:00 실행)
RUN echo "0 0 * * * cd /app && /usr/local/bin/python /app/crawler.py >> /var/log/cron.log 2>&1" > /etc/cron.d/crawler-cron && \
    chmod 0644 /etc/cron.d/crawler-cron && \
    crontab /etc/cron.d/crawler-cron && \
    touch /var/log/cron.log

# cron 실행 스크립트
COPY <<'EOF' /app/start.sh
#!/bin/bash
set -e

# 환경변수를 cron에 전달
env | grep -E 'GRPC_SERVER|NEWS_CRAWLER_API_KEY|LOG_LEVEL' > /etc/environment

# 초기 실행 (옵션)
echo "Initial crawler run..."
python /app/crawler.py

# cron 시작
echo "Starting cron..."
cron

# 로그 출력
tail -f /var/log/cron.log
EOF

RUN chmod +x /app/start.sh

CMD ["/app/start.sh"]
