# News Crawler 시스템 가이드

RSS 기반 부동산 뉴스 자동 수집 시스템

---

## 📋 목차

0. [실행방법](#실행방법)
1. [개요](#개요)
2. [목적](#목적)
3. [주요 기능](#주요-기능)
4. [기술 스택](#기술-스택)
5. [시스템 구조](#시스템-구조)
6. [구현 상세](#구현-상세)
7. [실행 방법](#실행-방법)
8. [신규 팀원 온보딩](#신규-팀원-온보딩)
9. [문제 해결](#문제-해결)

---

## 실행 방법

### 📋 사전 준비

1. **Docker Desktop 설치 및 실행**
  - Windows: https://www.docker.com/products/docker-desktop
  - Docker Desktop이 실행 중인지 확인

2. **환경 변수 확인**
   ```powershell
   # backend/.env.local 파일 존재 확인
   cd backend
   type .env.local
   
   # ENVIRONMENT, CRAWLER_BASE_URL, NEWS_CRAWLER_API_KEY 확인
   ```

---
### 🚀 개발환경 실행 순서(local)

#### 1단계: Backend 실행 (IntelliJ)
#### 2단계: Docker 컨테이너 실행 및 크롤러 로그 확인
```powershell
# 1. Backend 디렉토리로 이동
예) cd C:\Users\sypark1\IdeaProjects\bugmakers\backend

# 2. 컨테이너 중지
docker-compose down

# 3. Docker 시스템 정리 (안전) (안 쓰는 컨테이너, 이미지 찌꺼기 청소)
docker system prune -f

# 4. 재시작 with 환경파일
docker-compose --env-file .env.local up -d

# 5. Docker 컨테이너 상태 확인
docker ps

# 6. Docker 컨테이너 환경변수 확인하기
docker exec bugmaker-news-crawler env
docker exec bugmaker-postgres env 

# 7. Docker volume 확인하기
docker volume ls

# 8. 크롤링 1회 실행
docker exec bugmaker-news-crawler python crawler.py

# 9. 로그 확인하기
# 전체 로그
docker-compose logs

# 실시간 로그 확인
docker-compose logs -f news-crawler

# 크롤러 전체 로그 확인
docker-compose logs news-crawler

# 최근 100줄
docker-compose logs --tail=100 news-crawler

# 특정 시간 이후 로그
docker-compose logs --since 2026-01-13T09:00:00 news-crawler

* (참조) 볼륨 완전 삭제 
docker compose down -v 
또는 docker volume rm backend_postgres_data -f
```

---


### 🚀 운영환경 실행 순서(dev)

- 운영환경(dev) 배포하기
- 운영 환경에서 os cron 설정하기
```powershell
# 1. 서버 접속
ssh ubuntu@<dev domain url>

# 2. 프로젝트 클론
cd /home/ubuntu
git clone <repository-url>
cd bugmakers/scripts/news-crawler

# 3. Python 및 의존성 설치
python3 --version
sudo apt update
sudo apt install python3-pip -y
pip3 install -r requirements.txt

# 4. backend/.env.dev 확인 (선택)
cat ../../backend/.env.dev

# 5. Cron 설정
chmod +x deploy/setup_cron.sh
./deploy/setup_cron.sh

# "지금 테스트 실행하시겠습니까? (y/n):" 
# → y 입력 (테스트 실행)

# 6. 확인
# Cron 등록 확인
crontab -l

# 로그 확인
tail -f /var/log/news-crawler.log

```
- 운영환경 실행하기
```powershell
// 1. cron 목록 조회 & 실행 확인 
# 현재 사용자의 Cron 작업 목록 확인
crontab -l

# 예시 결과 : 
0 9,15,21 * * * cd /home/ubuntu/bugmakers/scripts/news-crawler && /usr/bin/python3 crawler.py >> /var/log/news-crawler.log 2>&1

# Cron 데몬이 실행 중인지 확인
sudo service cron status

# 현재 시간 확인
date

# 온라인 도구
https://crontab.guru
0 9,15,21 * * * 입력하면 설명 확인 가능

// 2. 운영 환경에서 크롤링 로그 확인하기 
# 실시간 로그 확인
tail -f /var/log/news-crawler.log

# 전체 로그 확인
cat /var/log/news-crawler.log

# 최근 100줄
tail -100 /var/log/news-crawler.log

# 오늘 로그
grep "$(date +%Y-%m-%d)" /var/log/news-crawler.log

# 특정 날짜 로그 (예: 2026-01-13)
grep "2026-01-13" /var/log/news-crawler.log

# 에러 로그만 보기
grep "ERROR" /var/log/news-crawler.log

# 에러 + 경고
grep -E "ERROR|WARNING" /var/log/news-crawler.log

# 성공한 뉴스만 보기
grep "✅ 뉴스 등록 성공" /var/log/news-crawler.log

# 중복 뉴스 확인
grep "⚠️  중복 뉴스" /var/log/news-crawler.log

# 크롤링 완료 통계 확인
grep "크롤링 완료" /var/log/news-crawler.log | tail -10

# 로그 크기 확인
ls -lh /var/log/news-crawler.log
```



---

## 개요
- 부동산 관련 뉴스를 RSS 피드에서 자동으로 수집하여 데이터베이스에 저장하는 시스템입니다. 
- Python 크롤러가 주기적으로 실행되어 뉴스를 수집하고, Spring Boot Backend API를 통해 PostgreSQL에 저장됩니다.

---

## 목적

### 문제점
- 부동산 뉴스를 수동으로 수집하는 것은 비효율적
- 여러 언론사의 뉴스를 한 곳에서 보기 어려움
- 카테고리 분류를 수동으로 해야 함

### 해결책
- **자동화**: RSS 크롤러가 자동으로 뉴스 수집
- **통합**: 여러 출처의 뉴스를 한 곳에 저장
- **분류**: 키워드 기반 자동 카테고리 분류 ([부동산], [정책], [시장], [금융])

---

## 주요 기능
### 1. RSS 크롤링
- 네이버, 다음, 매일경제, 한국경제 등 주요 언론사 RSS 피드 수집
- 동시 다발적 크롤링 (여러 출처 병렬 처리)

### 2. 자동 분류
- 키워드 매칭을 통한 카테고리 자동 분류
- 말머리 자동 추가 (예: `[부동산] 서울 아파트 가격 상승`)

### 3. 중복 방지
- 제목 기준 중복 체크
- 출처(URL) 기준 중복 체크
- Backend에서 이중 검증

### 4. 스케줄링
- 개발환경(local) : 수동 실행
- 컨테이너 재시작 시 즉시 크롤링 실행

- 운영환경(dev, prod) : os cron 
- 하루 3회 자동 실행 (09:00, 15:00, 21:00)

### 5. 로깅
- 실시간 로그 출력
- 파일 로그 저장 (`crawler.log`)
- 수집/저장 통계 제공

---

## 기술 스택

### Backend (기존 시스템)
- **언어**: Java 21
- **프레임워크**: Spring Boot 3.5.7
- **ORM**: JPA (Hibernate)
- **데이터베이스**: PostgreSQL 18
- **빌드 도구**: Gradle

### Crawler (신규 추가)
- **언어**: Python 3.11
- **주요 라이브러리**:
    - `feedparser` - RSS 파싱
    - `requests` - HTTP 요청
    - `PyYAML` - 설정 관리
    - `python-dotenv` - 환경 변수 로드
    - `colorlog` - 색상 로그 출력

### 인프라
- **컨테이너**: Docker & Docker Compose
- **네트워크**: Bridge 네트워크로 격리

---

## 시스템 구조

### 전체 아키텍처

```
┌─────────────────────────────────────────────────────────┐
│                    개발 환경 구성                        │
├─────────────────────────────────────────────────────────┤
│                                                         │
│  ┌───────────────┐         ┌──────────────┐             │
│  │  PostgreSQL   │ ←───────┤   Backend    │             │
│  │   (Docker)    │  JDBC   │  (IntelliJ)  │             │
│  │  Port: 5432   │         │  Port: 8080  │             │
│  └───────┬───────┘         └──────▲───────┘             │
│          │                         │                    │
│          │ SQL INSERT              │ HTTP POST          │
│          │                         │ /api/news          │
│          │                  ┌──────┴────────┐           │
│          │                  │     Python    │           │
│          │                  │    Crawler    │           │
│          │                  │    (Docker)   │           │
│          │                  └───────▲───────┘           │
│          │                          │                   │
│          │                          │ RSS Feed          │
│          │                  ┌───────┴───────┐           │
│          │                  │  네이버, 다음  │           │
│          └──────────────────│  매경, 한경 등 │           │
│                             └───────────────┘           │
│                                                         │
└─────────────────────────────────────────────────────────┘
```

### 프로젝트 디렉토리 구조

```
bugmakers/
├── backend/
│   ├── src/main/java/.../apt/
│   │   ├── controller/news/
│   │   │   └── NewsController.java         # REST API 엔드포인트
│   │   ├── service/news/
│   │   │   └── NewsService.java            # 비즈니스 로직
│   │   ├── repository/
│   │   │   └── NewsRepository.java         # JPA Repository
│   │   └── domain/news/
│   │       ├── News.java                   # 엔티티
│   │       ├── NewsCreateRequest.java      # DTO (Python → Java)
│   │       ├── NewsResponse.java           # DTO (목록용)
│   │       └── NewsDetailResponse.java     # DTO (상세용)
│   ├── compose.yaml                        # Docker Compose 설정
│   ├── .env.local                          # 환경 변수 (gitignore)
│   └── .env.dev                            # 개발 서버 환경 변수
│
└── scripts/
    └── news-crawler/
        ├── crawler.py                      # 메인 크롤러 로직
        ├── scheduler.py                    # 스케줄러 (로컬 개발용)
        ├── config.yaml                     # RSS 출처 & 키워드 설정
        ├── deploy/
        │   ├── setup_cron.sh              # 운영 서버 Cron 설정 스크립트
```

### 데이터 플로우

```
1. [RSS Feed] 
   │
   ├─ 네이버 부동산 RSS
   ├─ 다음 부동산 RSS  
   ├─ 매일경제 RSS
   └─ 한국경제 RSS
         ↓
2. [Python Crawler - crawler.py]
   - RSS 파싱 (feedparser)
   - 키워드 매칭 (부동산, 정책, 시장, 금융)
   - 말머리 추가 ([부동산], [정책] 등)
         ↓
3. [HTTP Request]
   - 로컬: POST http://host.docker.internal:8080/api/news
   - 운영: POST https://dev.koreavisited.shop/api/news
   (환경에 따라 BASE_URL 자동 변환)
   Header: X-API-Key
   Body: { title, content, reference }
         ↓
4. [Spring Boot Backend - NewsController]
   - API Key 검증
   - Validation (@Valid)
         ↓
5. [NewsService]
   - 중복 체크 (제목, 출처)
   - Entity 변환
         ↓
6. [NewsRepository]
   - JPA save()
         ↓
7. [PostgreSQL]
   - news 테이블에 저장
```

### Docker 네트워크 구조

```
Host Machine (Windows/Mac)
├── IntelliJ IDEA
│   └── Backend (localhost:8080)
│
└── Docker
    ├── Network: backend_bugmaker-network
    │   ├── Container: bugmaker-postgres
    │   │   └── Port: 5432 (exposed to host)
    │   │
    │   └── Container: bugmaker-news-crawler
    │       └── Connects to: host.docker.internal:8080
    │           (Docker의 특수 DNS로 Host의 Backend 접근)
```

**핵심 포인트**:
- `host.docker.internal`: Docker 컨테이너에서 Host 머신 접근용 특수 DNS
- Backend는 Docker 밖에서 실행되지만, 크롤러는 이 DNS로 접근 가능

---

## 구현 상세

### 1. Backend - NewsController.java

**위치**: `backend/src/main/java/com/bugmaker/apt/controller/news/NewsController.java`

**역할**: REST API 엔드포인트 제공

**주요 메서드**:

```java
// 1. 뉴스 등록 (Python 크롤러 전용)
@PostMapping
public ResponseEntity<NewsResponse> createNews(
    @RequestHeader("X-API-Key") String apiKey,
    @Valid @RequestBody NewsCreateRequest request)
```
- API Key 검증: `NEWS_CRAWLER_API_KEY`와 비교
- 실패 시: 401 Unauthorized 반환
- 성공 시: 201 Created + 생성된 뉴스 정보 반환

```java
// 2. 뉴스 목록 조회 (Frontend용)
@GetMapping
public ResponseEntity<Page<NewsResponse>> getNewsList(
    @RequestParam int page,
    @RequestParam int size)
```
- 최신순 정렬 (createdDate DESC)
- 페이징 처리
- 공개 API (인증 불필요)

```java
// 3. 뉴스 상세 조회
@GetMapping("/{id}")
public ResponseEntity<NewsDetailResponse> getNewsDetail(@PathVariable Long id)

// 4. 조회수 증가
@PostMapping("/{id}/view")

// 5. 좋아요 증가/취소
@PostMapping("/{id}/heart")
@DeleteMapping("/{id}/heart")
```

**환경 변수**:
```java
@Value("${news.crawler.api-key}")
private String crawlerApiKey;
```
- `application-local.yml`에서 로드

---

### 2. Backend - NewsService.java

**위치**: `backend/src/main/java/com/bugmaker/apt/service/news/NewsService.java`

**역할**: 비즈니스 로직 처리

**중복 체크 로직**:
```java
@Transactional
public NewsResponse createNews(NewsCreateRequest request) {
    // 1. 제목 중복 체크
    if (newsRepository.existsByTitle(request.title())) {
        throw new IllegalArgumentException("이미 등록된 뉴스입니다. (제목 중복)");
    }
    
    // 2. 출처 중복 체크
    if (newsRepository.existsByReference(request.reference())) {
        throw new IllegalArgumentException("이미 등록된 뉴스입니다. (출처 중복)");
    }
    
    // 3. 저장
    News news = request.toEntity();
    News savedNews = newsRepository.save(news);
    
    return NewsResponse.from(savedNews);
}
```

**트랜잭션 관리**:
- `@Transactional(readOnly = true)`: 클래스 레벨 (조회)
- `@Transactional`: 메서드 레벨 (저장/수정)

---

### 3. Backend - NewsRepository.java

**위치**: `backend/src/main/java/com/bugmaker/apt/repository/NewsRepository.java`

**역할**: 데이터베이스 접근

**주요 메서드**:
```java
// 페이징 조회
Page<News> findAll(Pageable pageable);

// 중복 체크
boolean existsByTitle(String title);
boolean existsByReference(String reference);

// 검색
Optional<News> findByTitle(String title);
Optional<News> findByReference(String reference);
```

**Spring Data JPA**:
- 인터페이스만 정의하면 구현체 자동 생성
- 메서드명 규칙으로 쿼리 자동 생성

---

### 4. Python - crawler.py

**위치**: `scripts/news-crawler/crawler.py`

**역할**: RSS 크롤링 메인 로직

**클래스 구조**:
```python
# 1. 로컬 .env 시도
load_dotenv()

# 2. backend/.env.dev 시도 (운영 환경)
backend_env_dev = Path(__file__).parent.parent.parent / "backend" / ".env.dev"
if backend_env_dev.exists() and not os.getenv('BASE_URL'):
    load_dotenv(backend_env_dev)

# 3. BASE_URL 사용 (통일!)
base_url = os.getenv('BASE_URL', 'http://localhost:8080')

# 4. Docker 환경 자동 감지
if 'localhost' in base_url and os.path.exists('/.dockerenv'):
    # Docker 컨테이너 내부 → host.docker.internal로 변환
    self.backend_url = base_url.replace('localhost', 'host.docker.internal')
else:
    self.backend_url = base_url
```

**주요 메서드**:

1. **RSS 피드 파싱**
```python
def fetch_rss_feed(self, source):
    feed = feedparser.parse(source['url'])
    
    for entry in feed.entries:
        title = entry.get('title')
        content = entry.get('summary')
        link = entry.get('link')
        
        # 카테고리 분류
        category = self.classify_category(title, content)
        if category:
            # 말머리 추가
            title_with_prefix = self.add_prefix(title, category)
            news_list.append({...})
```

2. **카테고리 분류**
```python
def classify_category(self, title, content):
    text = (title + " " + content).lower()
    
    # config.yaml의 keywords 참조
    for category, keyword_list in self.keywords.items():
        for keyword in keyword_list:
            if keyword in text:
                return category  # '부동산', '정책' 등
    
    return None  # 매칭 안 되면 제외
```

3. **Backend API 호출**
```python
def send_to_backend(self, news):
    url = f"{self.backend_url}/api/news"
    headers = {
        'X-API-Key': self.api_key,
        'Content-Type': 'application/json'
    }
    
    response = requests.post(url, json=data, headers=headers)
    
    if response.status_code == 201:
        logger.info("✅ 뉴스 등록 성공")
    elif response.status_code == 400:
        logger.warning("⚠️  중복 뉴스")
```

**실행 흐름**:
```python
def run(self):
    for source in self.rss_sources:
        # 1. RSS 피드 가져오기
        news_list = self.fetch_rss_feed(source)
        
        # 2. Backend로 전송
        for news in news_list:
            self.send_to_backend(news)
            time.sleep(1)  # 요청 간격
```

---

### 5. Python - config.yaml

**위치**: `scripts/news-crawler/config.yaml`

**역할**: RSS 출처 및 키워드 설정

**구조**:
```yaml
# RSS 출처
rss_sources:
  - name: "네이버 부동산"
    url: "https://news.naver.com/..."
    category: "부동산"
    enabled: true  # false로 변경 시 비활성화

# 키워드 (카테고리 분류용)
keywords:
  부동산:
    - "아파트"
    - "분양"
    - "청약"
  정책:
    - "정부"
    - "규제"

# 크롤링 설정
crawler:
  request_delay: 1  # 요청 간격 (초)
  max_news_per_run: 50  # 한 번에 수집할 최대 개수
```

**수정 방법**:
1. RSS 출처 추가: `rss_sources`에 항목 추가
2. 키워드 조정: `keywords`에 단어 추가/삭제
3. 설정 변경 후 컨테이너 재시작 필요

---

### 6. Docker - Dockerfile

**위치**: `scripts/news-crawler/Dockerfile`

**역할**: Python 크롤러 Docker 이미지 생성

**내용**:
```dockerfile
FROM python:3.11-slim

WORKDIR /app

# 의존성 설치
COPY requirements.txt .
RUN pip install --no-cache-dir -r requirements.txt

# 소스 코드 복사
COPY . .

# 환경 변수 (기본값)
ENV BACKEND_URL=http://backend:8080
ENV NEWS_CRAWLER_API_KEY=
ENV LOG_LEVEL=INFO

# 스케줄러 실행
CMD ["python", "scheduler.py"]
```

**빌드 과정**:
1. Python 3.11 이미지 기반
2. 의존성 설치 (requirements.txt)
3. 소스 코드 복사
4. 환경 변수 설정 (Docker Compose에서 오버라이드)
5. 컨테이너 시작 시 `scheduler.py` 실행

---

### 7. Docker - compose.yaml

**위치**: `backend/compose.yaml`

**역할**: 멀티 컨테이너 오케스트레이션

**전체 구조**:
```yaml
services:
  # PostgreSQL 데이터베이스
  postgres:
    image: 'postgres:latest'
    container_name: bugmaker-postgres
    ports:
      - '5432:5432'
  
  # Python 크롤러
  news-crawler:
    build:
      context: ../scripts/news-crawler
      dockerfile: Dockerfile
    container_name: bugmaker-news-crawler
    environment:
      - BASE_URL=${BASE_URL}                              # .env.local의 BASE_URL 사용
      - NEWS_CRAWLER_API_KEY=${NEWS_CRAWLER_API_KEY}
      - LOG_LEVEL=INFO
    restart: unless-stopped
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

**핵심 설정**:
- `BASE_URL=${BASE_URL}`: .env.local의 BASE_URL 자동 주입
- crawler.py가 localhost 감지 시 host.docker.internal로 자동 변환
- NEWS_CRAWLER_API_KEY=${NEWS_CRAWLER_API_KEY}`: .env.local에서 주입
- `restart: unless-stopped`: 컨테이너 자동 재시작
- `extra_hosts`: host.docker.internal DNS 설정

---

### 8. 환경 변수 관리

**Backend - .env.local**:
```env
BASE_URL=http://localhost:8080                              # ← 통일!
NEWS_CRAWLER_API_KEY=
```

**Backend - .env.dev**:
```env
BASE_URL=https://dev.koreavisited.shop                      # ← 통일!
NEWS_CRAWLER_API_KEY=
```

**Python Crawler**:
- 로컬: Docker Compose가 `BASE_URL` 환경변수 주입
- 운영: crawler.py가 직접 `backend/.env.dev`에서 `BASE_URL` 읽기

---
