#!/bin/bash

echo "========================================"
echo "News Crawler Cron 설정 (개발 서버)"
echo "========================================"
echo ""

# ========================================
# 1. 경로 확인
# ========================================
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
CRAWLER_DIR="$(dirname "$SCRIPT_DIR")"
BACKEND_ENV_DEV="$CRAWLER_DIR/../../backend/.env.dev"

echo "Crawler 경로: $CRAWLER_DIR"
echo "Backend .env.dev: $BACKEND_ENV_DEV"
echo ""

# ========================================
# 2. Python 확인
# ========================================
PYTHON_PATH=$(which python3)
if [ -z "$PYTHON_PATH" ]; then
    echo "[에러] python3를 찾을 수 없습니다."
    echo "설치: sudo apt install python3 python3-pip"
    exit 1
fi

echo "Python 경로: $PYTHON_PATH"
echo ""

# ========================================
# 3. backend/.env.dev 확인
# ========================================
if [ ! -f "$BACKEND_ENV_DEV" ]; then
    echo "[에러] backend/.env.dev 파일을 찾을 수 없습니다."
    echo "경로: $BACKEND_ENV_DEV"
    exit 1
fi

echo "✅ backend/.env.dev 파일 확인 완료"
echo ""

# ========================================
# 4. 환경 변수 확인
# ========================================
BASE_URL=$(grep "^BASE_URL=" "$BACKEND_ENV_DEV" | cut -d '=' -f2)
NEWS_CRAWLER_API_KEY=$(grep "^NEWS_CRAWLER_API_KEY=" "$BACKEND_ENV_DEV" | cut -d '=' -f2)

if [ -z "$BASE_URL" ]; then
    echo "[에러] BASE_URL을 찾을 수 없습니다."
    echo ""
    echo "backend/.env.dev 파일을 확인하세요:"
    cat "$BACKEND_ENV_DEV"
    exit 1
fi

if [ -z "$NEWS_CRAWLER_API_KEY" ]; then
    echo "[에러] NEWS_CRAWLER_API_KEY를 찾을 수 없습니다."
    echo ""
    echo "backend/.env.dev 파일을 확인하세요:"
    cat "$BACKEND_ENV_DEV"
    exit 1
fi

echo "환경 변수 확인:"
echo "  BASE_URL: $BASE_URL"
echo "  NEWS_CRAWLER_API_KEY: ${NEWS_CRAWLER_API_KEY:0:10}..." # 앞 10자만 표시
echo ""

# ========================================
# 5. 로그 파일 생성
# ========================================
LOG_FILE="/var/log/news-crawler.log"

if [ ! -f "$LOG_FILE" ]; then
    echo "로그 파일 생성 중..."
    sudo touch "$LOG_FILE"
    sudo chown ubuntu:ubuntu "$LOG_FILE"
    sudo chmod 644 "$LOG_FILE"
    echo "✅ 로그 파일 생성: $LOG_FILE"
else
    echo "✅ 로그 파일 존재: $LOG_FILE"
fi
echo ""

# ========================================
# 6. Cron 작업 생성
# ========================================
CRON_JOB="0 9,15,21 * * * cd $CRAWLER_DIR && $PYTHON_PATH crawler.py >> $LOG_FILE 2>&1"

# 기존 Cron 확인
if crontab -l 2>/dev/null | grep -q "crawler.py"; then
    echo "[경고] 이미 크롤러 Cron이 등록되어 있습니다."
    echo ""
    echo "현재 등록된 Cron:"
    crontab -l | grep crawler.py
    echo ""
    read -p "덮어쓰시겠습니까? (y/n): " -n 1 -r
    echo ""
    if [[ ! $REPLY =~ ^[Yy]$ ]]; then
        echo "취소되었습니다."
        exit 0
    fi
    # 기존 제거
    crontab -l | grep -v "crawler.py" | crontab -
    echo "기존 Cron 제거 완료"
    echo ""
fi

# Cron 등록
(crontab -l 2>/dev/null; echo "$CRON_JOB") | crontab -

echo "✅ Cron 등록 완료!"
echo ""
echo "================================================"
echo "설정 완료!"
echo "================================================"
echo ""
echo "등록된 스케줄:"
echo "  - 매일 오전 09:00 (한국 시간)"
echo "  - 매일 오후 15:00 (한국 시간)"
echo "  - 매일 오후 21:00 (한국 시간)"
echo ""
echo "환경 변수:"
echo "  - BASE_URL: $BASE_URL"
echo "  - NEWS_CRAWLER_API_KEY: (설정됨)"
echo ""
echo "로그 위치: $LOG_FILE"
echo ""
echo "================================================"
echo "확인 명령어:"
echo "================================================"
echo "  Cron 확인:      crontab -l"
echo "  로그 확인:      tail -f $LOG_FILE"
echo "  즉시 실행:      cd $CRAWLER_DIR && python3 crawler.py"
echo "  Cron 서비스:    sudo service cron status"
echo ""

# ========================================
# 7. 즉시 테스트 실행 (선택)
# ========================================
read -p "지금 테스트 실행하시겠습니까? (y/n): " -n 1 -r
echo ""
if [[ $REPLY =~ ^[Yy]$ ]]; then
    echo ""
    echo "크롤러 테스트 실행 중..."
    echo "================================================"
    cd "$CRAWLER_DIR" && $PYTHON_PATH crawler.py
    echo ""
    echo "================================================"
    echo "테스트 완료!"
    echo "로그 확인: tail -20 $LOG_FILE"
fi