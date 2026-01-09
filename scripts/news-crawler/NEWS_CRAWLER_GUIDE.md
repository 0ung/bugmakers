# News Crawler 시스템 가이드

RSS 기반 부동산 뉴스 자동 수집 시스템

---

## 📋 목차

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

## 개요

부동산 관련 뉴스를 RSS 피드에서 자동으로 수집하여 데이터베이스에 저장하는 시스템입니다. Python 크롤러가 주기적으로 실행되어 뉴스를 수집하고, Spring Boot Backend API를 통해 PostgreSQL에 저장됩니다.

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
//TODO 스케쥴링 자동주기 임의 셋팅. 나머지 기능도 수정/보완 할 곳 많음. 일단 정리만 해둠

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
- 하루 3회 자동 실행 (09:00, 15:00, 21:00)
- 컨테이너 재시작 시 즉시 크롤링 실행

### 5. 로깅
- 실시간 로그 출력
- 파일 로그 저장 (`crawler.log`, `scheduler.log`)
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
    - `schedule` - 스케줄링
    - `PyYAML` - 설정 관리

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
        ├── scheduler.py                    # 스케줄러
        ├── config.yaml                     # RSS 출처 & 키워드 설정
        ├── requirements.txt                # Python 의존성
        ├── Dockerfile                      # Docker 이미지
        └── .dockerignore                   # Docker 빌드 제외 파일
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
   POST http://host.docker.internal:8080/api/news
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
class NewsCrawler:
    def __init__(self, config_path='config.yaml'):
        # 환경변수 로드
        self.backend_url = os.getenv('BACKEND_URL')
        self.api_key = os.getenv('NEWS_CRAWLER_API_KEY')
        
        # 설정 파일 로드
        self.config = yaml.safe_load(open(config_path))
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

### 5. Python - scheduler.py

**위치**: `scripts/news-crawler/scheduler.py`

**역할**: 주기적 크롤링 실행

**스케줄 설정**:
```python
# 매일 오전 9시, 오후 3시, 오후 9시 실행
schedule.every().day.at("09:00").do(job)
schedule.every().day.at("15:00").do(job)
schedule.every().day.at("21:00").do(job)

# 시작 시 즉시 한 번 실행
job()

# 무한 루프
while True:
    schedule.run_pending()
    time.sleep(60)  # 1분마다 체크
```

**작업 함수**:
```python
def job():
    try:
        crawler = NewsCrawler()
        crawler.run()
    except Exception as e:
        logger.error(f"❌ 스케줄 작업 실패: {e}")
```

---

### 6. Python - config.yaml

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

### 7. Docker - Dockerfile

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

### 8. Docker - compose.yaml

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
      - BACKEND_URL=http://host.docker.internal:8080
      - NEWS_CRAWLER_API_KEY=${NEWS_CRAWLER_API_KEY}
    restart: unless-stopped
    extra_hosts:
      - "host.docker.internal:host-gateway"
```

**핵심 설정**:
- `context: ../scripts/news-crawler`: 빌드 컨텍스트 경로
- `BACKEND_URL=http://host.docker.internal:8080`: Host의 Backend 접근
- `NEWS_CRAWLER_API_KEY=${NEWS_CRAWLER_API_KEY}`: .env.local에서 주입
- `restart: unless-stopped`: 컨테이너 자동 재시작
- `extra_hosts`: host.docker.internal DNS 설정

---

### 9. 환경 변수 관리

**Backend - .env.local**:
**Backend - application-local.yml**:
- `${변수명:기본값}` 형식
- 환경변수 없으면 기본값 사용

**환경변수 주입 흐름**:
```
.env.local 파일
    ↓ docker-compose --env-file .env.local
Docker Compose
    ↓ environment 설정
Python 컨테이너
    ↓ os.getenv()
crawler.py
```

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
   
   # NEWS_CRAWLER_API_KEY 확인
   ```

---

### 🚀 실행 순서

#### 1단계: Docker 컨테이너 실행

```powershell
# backend 디렉토리로 이동
cd C:\Users\sypark1\IdeaProjects\bugmakers\backend

# Docker Compose 실행 (.env.local 사용)
docker-compose --env-file .env.local up -d

# 실행 확인
docker-compose ps
```

**예상 출력**:
```
NAME                       STATUS
bugmaker-postgres          Up
bugmaker-news-crawler      Up
```

**주의사항**:
- `--env-file .env.local` 옵션 필수!
- 옵션 없으면 환경변수가 주입되지 않음

---

#### 2단계: Backend 실행 (IntelliJ)

```
1. IntelliJ IDEA 실행
2. AptApplication.java 우클릭
3. Run 'AptApplication' 클릭
```

**실행 확인**:
```
콘솔에 다음 메시지 확인:
Started AptApplication in X.XXX seconds (JVM running for X.XXX)
```

**포트 확인**:
```
Tomcat started on port 8080
```

---

#### 3단계: 크롤러 로그 확인

```powershell
# 크롤러 로그 실시간 확인
docker-compose logs -f news-crawler
```

**성공 시 로그**:
```
============================================================
뉴스 크롤링 시작
============================================================
RSS 피드 가져오기: 네이버 부동산
네이버 부동산: 0개 뉴스 수집
RSS 피드 가져오기: 매일경제
매일경제: 27개 뉴스 수집
✅ 뉴스 등록 성공: [부동산] 서울 아파트 가격 상승...
✅ 뉴스 등록 성공: [정책] 정부, 청약 규제 완화...
⚠️  중복 뉴스: [시장] 전세가율 70% 돌파...
============================================================
크롤링 완료 - 수집: 27개, 저장: 15개
============================================================
```

**로그 종료**: `Ctrl + C`

---

#### 4단계: 뉴스 데이터 확인

##### A. API로 확인 (브라우저)

```
http://localhost:8080/api/news?page=0&size=10
```

**예상 응답**:
```json
{
  "content": [
    {
      "id": 1,
      "title": "[부동산] 서울 아파트 가격 5주 연속 상승",
      "reference": "https://...",
      "viewCount": 0,
      "heartCount": 0,
      "createdDate": "2026-01-09T10:30:00"
    }
  ],
  "totalElements": 15,
  "totalPages": 2
}
```

##### B. DB에서 확인

```powershell
# PostgreSQL 접속
docker-compose exec postgres psql -U postgres -d bugmaker_local

# 뉴스 조회
SELECT id, title, created_date FROM news ORDER BY created_date DESC LIMIT 10;

# 종료
\q
```

---

### 🛑 중지 방법

```powershell
# Docker 컨테이너 중지
docker-compose down

# Backend 중지 (IntelliJ에서 Stop 버튼)
```

---

### 🔄 재시작 방법

#### 전체 재시작
```powershell
docker-compose down
docker-compose --env-file .env.local up -d
```

#### 크롤러만 재시작 (코드 수정 후)
```powershell
docker-compose restart news-crawler
```

#### 크롤러 재빌드 (Dockerfile 수정 후)
```powershell
docker-compose build news-crawler
docker-compose up -d news-crawler
```

---

### 🧪 수동 크롤링 실행

```powershell
# 스케줄 대기 없이 즉시 크롤링 실행
docker-compose exec news-crawler python crawler.py
```

---

### 🔍 로그 확인 방법

```powershell
# 전체 로그
docker-compose logs

# 크롤러 로그만
docker-compose logs news-crawler

# 실시간 로그
docker-compose logs -f news-crawler

# 최근 100줄
docker-compose logs --tail=100 news-crawler
```

---

## 신규 팀원 온보딩

### 📥 프로젝트 Clone 후 설정

#### 1단계: 저장소 Clone

```bash
git clone <repository-url>
cd bugmakers
```

---

#### 2단계: 환경 변수 설정

```bash
cd backend

# .env.local 파일 생성 (없는 경우)
# 팀 리더에게 실제 KEY 값 전달받기
```

**⚠️  보안 주의**:
- `.env.local` 파일은 절대 Git에 커밋하지 않기
- `.gitignore`에 등록되어 있는지 확인

---

#### 3단계: Docker Desktop 설치

1. Docker Desktop 다운로드 및 설치
    - https://www.docker.com/products/docker-desktop

2. Docker Desktop 실행 확인
   ```powershell
   docker --version
   docker-compose --version
   ```

---

#### 4단계: 프로젝트 실행

```powershell
# 1. Docker 컨테이너 시작
cd backend
docker-compose --env-file .env.local up -d

# 2. IntelliJ에서 Backend 실행
# Run → Run 'AptApplication'

# 3. 로그 확인
docker-compose logs -f news-crawler
```

---

#### 5단계: 동작 확인

```
1. 브라우저에서 뉴스 목록 확인:
   http://localhost:8080/api/news

2. 크롤러 로그에서 "뉴스 등록 성공" 메시지 확인
```

---

### 🎓 알아두면 좋은 것

#### Python 코드 수정 시

1. 코드 수정
2. 컨테이너 재시작:
   ```powershell
   docker-compose restart news-crawler
   ```

#### config.yaml 수정 시 (RSS 출처 추가 등)

1. `scripts/news-crawler/config.yaml` 수정
2. 컨테이너 재시작:
   ```powershell
   docker-compose restart news-crawler
   ```

#### Dockerfile 수정 시

1. Dockerfile 수정
2. 재빌드 및 재시작:
   ```powershell
   docker-compose build news-crawler
   docker-compose up -d news-crawler
   ```

---

## 문제 해결

### 🐛 자주 발생하는 문제

#### 1. "API Key 인증 실패"

**증상**:
```
❌ 인증 실패: API Key 확인 필요
```

**원인**: API Key 불일치

**해결**:
```powershell
# 1. .env.local 확인
type .env.local
# NEWS_CRAWLER_API_KEY 값 확인

# 2. Docker 재시작
docker-compose down
docker-compose --env-file .env.local up -d

# 3. Backend 재시작 (IntelliJ)
```

---

#### 2. "Backend 연결 실패"

**증상**:
```
❌ API 요청 실패: Connection refused
```

**원인**: Backend가 실행되지 않음

**해결**:
```powershell
# 1. Backend 포트 확인
netstat -ano | findstr :8080

# 2. Backend 실행 확인 (IntelliJ 콘솔)
# "Started AptApplication" 메시지 확인

# 3. 브라우저에서 확인
# http://localhost:8080/api/news
```

---

#### 3. "뉴스가 수집되지 않음"

**증상**:
```
매일경제: 0개 뉴스 수집
```

**원인**: 키워드 매칭 실패 또는 RSS 피드 변경

**해결**:
```yaml
# config.yaml 키워드 확인 및 추가
keywords:
  부동산:
    - "아파트"
    - "분양"
    # 더 많은 키워드 추가
```

**디버깅**:
```python
# crawler.py에 로그 추가
logger.debug(f"제목: {title}, 카테고리: {category}")
```

---

#### 4. "중복 뉴스만 발생"

**증상**:
```
⚠️  중복 뉴스: [부동산] ...
⚠️  중복 뉴스: [부동산] ...
```

**원인**: 이미 수집된 뉴스 (정상)

**확인**:
```sql
-- 마지막 수집 시간 확인
SELECT MAX(created_date) FROM news;
```

**해결**: 문제 없음 (중복 방지가 정상 작동 중)

---

#### 5. "Docker 컨테이너가 시작 안 됨"

**증상**:
```powershell
docker-compose ps
# news-crawler    Exit 1
```

**원인**: Python 코드 오류 또는 환경변수 문제

**해결**:
```powershell
# 1. 로그 확인
docker-compose logs news-crawler

# 2. 직접 실행하여 에러 확인
docker-compose run --rm news-crawler python crawler.py

# 3. 환경변수 확인
docker-compose config
```

---

#### 6. "PostgreSQL 데이터가 사라짐"

**원인**: `docker-compose down -v` 실행 (볼륨 삭제)

**해결**:
```powershell
# 볼륨 삭제 없이 중지
docker-compose down

# 데이터 백업 (중요한 경우)
docker-compose exec postgres pg_dump -U postgres bugmaker_local > backup.sql
```

---

### 🔧 디버깅 팁

#### 크롤러 내부 확인

```powershell
# 컨테이너 내부 접속
docker-compose exec news-crawler sh

# Python 직접 실행
python crawler.py

# 환경변수 확인
echo $BACKEND_URL
echo $NEWS_CRAWLER_API_KEY

# 종료
exit
```

#### 네트워크 연결 확인

```powershell
# 크롤러에서 Backend 연결 테스트
docker-compose exec news-crawler ping host.docker.internal

# 포트 확인
docker-compose exec news-crawler curl http://host.docker.internal:8080/api/news
```

---

## 📚 참고 자료

### 공식 문서
- [Spring Boot](https://spring.io/projects/spring-boot)
- [Docker Compose](https://docs.docker.com/compose/)
- [Feedparser](https://feedparser.readthedocs.io/)
- [Python Requests](https://requests.readthedocs.io/)

### 내부 문서
- Backend API 명세: (추가 예정)
- 배포 가이드: (추가 예정)

---

## 📝 변경 이력

| 날짜 | 버전 | 변경 내용 | 작성자 |
|------|------|-----------|--------|
| 2026-01-09 | 1.0.0 | 초기 작성 | pk |

---

## 🤝 기여하기

### RSS 출처 추가

1. `scripts/news-crawler/config.yaml` 수정
2. `rss_sources`에 새 출처 추가
3. 테스트 후 PR 생성

### 키워드 개선

1. 수집된 뉴스 분석
2. 누락된 키워드 파악
3. `keywords` 섹션에 추가

---

**작성일**: 2026-01-09  
**버전**: 1.0.0  
**문의**: 프로젝트 리더에게 문의
