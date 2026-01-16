# News Crawler 시스템 가이드

RSS 기반 부동산 뉴스 자동 수집 시스템 (gRPC)

---

## 📋 목차

1. [실행 방법 & 배포 방법](#실행-방법--배포-방법)
2. [기능설명](#기능설명)
3. [기술 스택](#기술-스택)
4. [통신 프로토콜 (gRPC)](#통신-프로토콜-grpc)
5. [통신 방식 비교 (REST vs gRPC)](#통신-방식-비교-rest-vs-grpc)
6. [구현 상세](#구현-상세)
7. [시스템 구조](#시스템-구조)
---

## 실행 방법 & 배포 방법
### 🚀 개발환경 실행 순서 (Local - gRPC)
#### 1단계: Docker Desktop 설치 및 실행
#### 2단계: Backend 실행 (IntelliJ)
**확인**:
```
✅ Started AptApplication in X.XXX seconds
✅ Tomcat started on port 8080 (REST API)
✅ gRPC Server started on port 9090 (Crawler용)
```
---
#### 3단계: Docker 컨테이너 실행
```powershell
# 1. Backend 디렉토리로 이동

# 2. 컨테이너 중지
docker-compose down

# 3. Docker 시스템 정리 (선택)
docker system prune -f

# 4. 재시작
docker-compose --env-file .env.local up -d

# 5. Docker 컨테이너 상태 확인
docker ps

# 6. 환경변수 확인
docker exec bugmaker-news-crawler env | findstr BASE_URL

# 7. Docker volume 확인
docker volume ls
```
---
#### 4단계: Proto 생성
```powershell
# Proto 파일 자동 생성
docker exec bugmaker-news-crawler python generate_proto.py

# 예상 로그: ✅ Proto 생성 성공!
```
---
#### 5단계: 크롤링 테스트
```powershell
# 크롤링 1회 실행
docker exec bugmaker-news-crawler python crawler.py

# 예상 로그:
# 크롤링 완료 - 수집: 10개, 저장: 8개
```
---
#### 6단계: 로그 확인
```powershell
# 실시간 로그 확인
docker-compose logs -f news-crawler

# 전체 로그
docker-compose logs news-crawler

# 최근 100줄
docker-compose logs --tail=100 news-crawler

# 특정 시간 이후 로그
docker-compose logs --since 2026-01-15T09:00:00 news-crawler

# 전체 컨테이너 로그
docker-compose logs
```
**참고**: 볼륨 완전 삭제
```powershell
docker-compose down -v
# 또는
docker volume rm backend_postgres_data -f
```
---

### 🚀 운영환경 실행 순서 (Dev - gRPC)
#### 1단계: 배포하기
```bash
# 1. 서버 접속
ssh ubuntu@<dev domain url>

# 2. 프로젝트 클론
cd /home/ubuntu
git clone <repository-url>
cd bugmakers

# 3. 환경 파일 확인
cat backend/.env.dev
## 없으면 생성 후 입력하기 nano .env.dev

# 4. Backend 빌드 및 실행
cd ../../backend
./gradlew clean build
nohup java -jar build/libs/apt-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  > backend.log 2>&1 &
# 로그 확인
tail -f backend.log

# 확인:
# ✅ Started AptApplication
# ✅ Tomcat started on port 8080
# ✅ gRPC Server started on port 9090

# 5. Python 및 의존성 설치
cd scripts/news-crawler
python3 --version
sudo apt update
sudo apt install python3-pip -y
pip3 install -r requirements.txt

# 6. Proto 생성 (Python 직접)
cd ../scripts/news-crawler
python3 generate_proto.py

# 예상 로그: ✅ Proto 생성 성공!

# 7. 크롤링 테스트
python3 crawler.py

# 예상 로그:
# 🚀 gRPC 클라이언트 초기화 완료
# ✅ [gRPC] 뉴스 등록 성공 - ID: 1
```
---
#### 2단계: OS Cron 설정하기
```bash
# 1. Cron 설정 스크립트 실행
cd /home/ubuntu/bugmakers/scripts/news-crawler
chmod +x deploy/setup_cron.sh
./deploy/setup_cron.sh
# "지금 테스트 실행하시겠습니까? (y/n):" → y 입력 (테스트 실행)

# 2. Cron 확인
crontab -l

# 예상 결과:
# 0 9,15,21 * * * cd /home/ubuntu/bugmakers/scripts/news-crawler && /usr/bin/python3 crawler.py >> /var/log/news-crawler.log 2>&1

# 3. Cron 데몬 확인
sudo service cron status

# 4. 현재 시간 확인
date

# 5. Cron 스케줄 확인 (온라인 도구)
# https://crontab.guru
# 0 9,15,21 * * * 입력 → "At 09:00, 15:00, and 21:00"
```
---
### 코드 수정 시 배포 (업데이트)
#### 1. Crawler만 변경
```bash
cd /home/ubuntu/bugmakers
git pull origin main

# Proto 변경 시
cd scripts/news-crawler
python3 generate_proto.py

# 테스트
python3 crawler.py
```
---

#### 2. Backend만 변경
```bash
cd /home/ubuntu/bugmakers
git pull origin main

cd backend
./gradlew clean build

# 재시작
pkill -f apt-0.0.1-SNAPSHOT.jar
nohup java -jar build/libs/apt-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  > backend.log 2>&1 &
```
---

#### 3. Proto 파일 변경
```bash
# Backend 재빌드 → 재시작
cd /home/ubuntu/bugmakers/backend
./gradlew clean build
pkill -f apt-0.0.1-SNAPSHOT.jar
nohup java -jar build/libs/apt-0.0.1-SNAPSHOT.jar \
  --spring.profiles.active=dev \
  > backend.log 2>&1 &

# Crawler Proto 재생성
cd ../scripts/news-crawler
python3 generate_proto.py

# 테스트
python3 crawler.py
```
---

#### 참고: 로그 확인하기
```bash
# 실시간 로그 확인
tail -f /var/log/news-crawler.log

# 전체 로그
cat /var/log/news-crawler.log

# 최근 100줄
tail -100 /var/log/news-crawler.log

# 오늘 로그
grep "$(date +%Y-%m-%d)" /var/log/news-crawler.log

# 특정 날짜 로그 (예: 2026-01-15)
grep "2026-01-15" /var/log/news-crawler.log

# 에러 로그만 보기
grep "ERROR" /var/log/news-crawler.log

# 에러 + 경고
grep -E "ERROR|WARNING" /var/log/news-crawler.log

# gRPC 성공 로그
grep "\[gRPC\] 뉴스 등록 성공" /var/log/news-crawler.log

# 중복 뉴스 확인
grep "중복 뉴스" /var/log/news-crawler.log

# 크롤링 완료 통계 확인
grep "크롤링 완료" /var/log/news-crawler.log | tail -10

# 로그 크기 확인
ls -lh /var/log/news-crawler.log
```
---

## 기능설명
### 개요
부동산 관련 뉴스를 RSS 피드에서 자동으로 수집하여 데이터베이스에 저장하는 시스템입니다.

**통신 방식**: gRPC (HTTP/2, Protocol Buffers) - 고성능 RPC 프레임워크

Python 크롤러가 주기적으로 실행되어 뉴스를 수집하고, Spring Boot Backend의 gRPC 서비스를 통해 PostgreSQL에 저장됩니다.

---

### 문제점
- 부동산 뉴스를 수동으로 수집하는 것은 비효율적
- 여러 언론사의 뉴스를 한 곳에서 보기 어려움
- 카테고리 분류를 수동으로 해야 함
- 대량의 뉴스 데이터 전송 시 성능 문제

### 해결책
- **자동화**: RSS 크롤러가 자동으로 뉴스 수집
- **통합**: 여러 출처의 뉴스를 한 곳에 저장
- **분류**: 키워드 기반 자동 카테고리 분류 ([부동산], [정책], [시장], [금융])
- **고성능 통신**: gRPC로 빠르고 효율적인 데이터 전송 (REST 대비 3-5배 빠름)

---

### 주요 기능
#### 1. RSS 크롤링
- 네이버, 다음, 매일경제, 한국경제 등 주요 언론사 RSS 피드 수집
- 동시 다발적 크롤링 (여러 출처 병렬 처리)

#### 2. 자동 분류
- 키워드 매칭을 통한 카테고리 자동 분류
- 말머리 자동 추가 (예: `[부동산] 서울 아파트 가격 상승`)

#### 3. 중복 방지
- 제목 기준 중복 체크
- 출처(URL) 기준 중복 체크
- Backend에서 이중 검증

#### 4. 고성능 gRPC 통신
- **HTTP/2 기반** 바이너리 프로토콜
- **Protocol Buffers** 직렬화 (JSON 대비 40% 크기 감소)
- **처리 속도** REST 대비 3-5배 향상
- **타입 안정성** Proto 정의로 계약 보장

#### 5. 스케줄링
- **개발환경(Local)**: 수동 실행 (docker exec)
- **운영환경(Dev/Prod)**: OS cron - 하루 3회 자동 실행 (09:00, 15:00, 21:00)

#### 6. 로깅
- 실시간 로그 출력
- 파일 로그 저장 (`crawler.log`, `/var/log/news-crawler.log`)
- 수집/저장 통계 제공

---

### 기술 스택
#### Backend
- **언어**: Java 21
- **프레임워크**: Spring Boot 3.5.7
- **ORM**: JPA (Hibernate)
- **데이터베이스**: PostgreSQL 15
- **빌드 도구**: Gradle
- **통신**: REST API (Frontend용) + gRPC Server (Crawler용)

#### Crawler
- **언어**: Python 3.11
- **주요 라이브러리**:
  - `feedparser` - RSS 파싱
  - `grpcio` - gRPC 클라이언트
  - `grpcio-tools` - Proto 컴파일
  - `PyYAML` - 설정 관리
  - `python-dotenv` - 환경 변수 로드
  - `colorlog` - 색상 로그 출력

#### 인프라
- **컨테이너**: Docker & Docker Compose (Local 환경)
- **프로토콜**: gRPC (HTTP/2 + Protocol Buffers)
- **스케줄러**: Docker (Local), OS Cron (Dev/Prod)


#### 환경별 비교표
| 항목 | Local | Dev |
|-----|-------|-----|
| **Backend 실행 위치** | Host (IntelliJ) | Host (JAR) |
| **Crawler 실행 위치** | **Docker** | **Host (Python 직접)** |
| **PostgreSQL 위치** | Docker | 외부 서버 (43.201.77.236) |
| **Backend 포트** | 8080 (REST), 9090 (gRPC) | 8080 (REST), 9090 (gRPC) |
| **Crawler가 보는 Backend** | `host.docker.internal:9090` | `localhost:9090` |
| **통신 프로토콜** | gRPC | gRPC |
| **실행 방식** | 수동 (`docker exec`) | 자동 (OS cron) |
| **Proto 생성** | Docker 내부 | Python 직접 |
| **환경 파일** | `.env.local` | `.env.dev` |

---

## 통신 프로토콜 (gRPC)
### gRPC란?
- Google이 개발한 고성능 오픈소스 **RPC(Remote Procedure Call)** 프레임워크
- **RPC(Remote Procedure Call)** : 원격에 있는 함수를 마치 로컬 함수처럼 호출하는 기술

예) JAVA에서의 일반 함수 호출:
```java
  // 같은 프로그램 내부
  NewsResponse response = newsService.createNews(request);
```
예) Python에서의 RPC (원격 함수 호출):
```python
  # Python에서 Java 함수를 마치 로컬처럼 호출!
  response = stub.CreateNews(request)  
  # 실제로는 네트워크 너머 Java Backend 함수 실행
```

- 왜 gRPC를 만들었나?
#### 기존 REST API의 문제점
1. **느린 JSON 처리**
  - JSON은 텍스트 기반 → 파싱 느림
  - 데이터 크기가 큼 (공백, 필드명 반복)

2. **HTTP/1.1의 한계**
  - 요청마다 새로운 연결 생성 (반드시 1:1, 단방향)
  - Head-of-Line Blocking (앞 요청이 막으면 뒤도 막힘)

3. **타입 안정성 없음**
  - API 문서와 실제 구현 불일치 가능
  - 런타임에만 에러 발견

4. **수동 코드 작성**
  - 클라이언트 코드 직접 작성
  - API 변경 시 수동 동기화

#### gRPC의 해결책
1. **Protocol Buffers (Protobuf)**
  - 바이너리 직렬화 → 빠르고 작은 메시지 
  - 예: JSON 500bytes → Protobuf 200bytes (약 60% 압축)
  - 언어 중립 IDL 제공 → 다양한 언어(Java, Python, Go, C++ 등) 자동 지원

2. **HTTP/2 기반**
  - 멀티플렉싱: 하나의 연결에서 다중 요청/응답 동시 처리
  - (하나의 TCP 연결 위에 Stream 개념을 올림. 클라이언트 ↔ 서버가 동시에 메시지를 주고받을 수 있음.)
  - 헤더 압축: 반복 헤더 전송 비용 감소
  - 서버 푸시 지원
  - 스트림 기반 통신 → 양방향 스트리밍(Bidirectional Streaming) 지원

3. **강력한 타입 시스템**
  - .proto 파일로 API 계약(Contract) 명확히 정의
  - 컴파일 타임 타입 검증으로 런타임 오류 감소
  - 클라이언트–서버 간 인터페이스 불일치 방지

4. **자동 코드 생성**
  - Proto 파일 → 클라이언트/서버 코드 자동 생성
  - API 변경 시 코드 재생성으로 변경 사항 자동 반영
  - 다중 언어 환경에서도 API 일관성 유지

---

## 통신 방식 비교 (REST vs gRPC)
### 데이터 변환 과정
- HTTP REST
```
Python Dict → JSON Text → Java Object
  (메모리)      (~500B)     (메모리)
```

- gRPC
```
Python Object → Proto Binary → Java Proto → Java Object
  (메모리)        (~200B)      (메모리)     (메모리)
                  ↑
              40% 압축!
```

### REST vs gRPC 성능 비교
| 항목           | HTTP REST         | gRPC              | 개선        |
|--------------|-------------------|-------------------|-----------|
| **프로토콜**     | HTTP/1.1          | HTTP/2            | 멀티플렉싱     |
| **데이터 형식**   | JSON(Text)        | Protobuf(Binary)  | 40% 감소    |
| **데이터 크기**   | ~500 bytes        | ~200 bytes        | 40% 감소    |
| **직렬화 속도**   | 느림 (JSON 파싱 오버헤드) | 빠름 (Binary 직접 읽기) | 3배 빠름     |
| **처리 시간**    | 50-100ms          | 10-30ms           | **3-5배 빠름** |
| **1000개 처리** | ~60초              | ~20초              | **3배 빠름** |
| **평균 처리율**   | 16 req/sec        | 50 req/sec        | **3-5배 빠름** |
| **타입 안정성**   | 없음 (런타임 검증) | 강함 (컴파일 타임 검증) | 사전 오류 방지 |
| **코드 생성** | 수동 | 자동 (Proto → Code) | 개발 생산성 향상 |
| **브라우저 지원** | ✅ 완벽 지원 | ❌ 제한적 (gRPC-Web 필요) | REST 유리 |
| **디버깅** | 쉬움 (Postman, 브라우저) | 어려움 (전용 도구 필요) | REST 유리 |
| **스트리밍** | 제한적 (WebSocket 필요) | ✅ 양방향 스트리밍 내장 | 실시간 처리 가능 |
| **적합한 용도** | Frontend ↔ Backend | Backend ↔ Backend (마이크로서비스) | 아키텍처 분리 |

### REST vs gRPC 통신 방식 비교 (다이어그램)
#### 1. HTTP REST 방식
```
┌─────────────────────────────────────────────────────────────────┐
│ Ubuntu Server (Host)                                            │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Python Crawler (crawler.py)                            │   │
│  │ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │   │
│  │                                                        │   │
│  │ 1. RSS 수집 & 파싱                                      │   │
│  │    ├─ feedparser.parse()                              │   │
│  │    ├─ 키워드 매칭                                       │   │
│  │    └─ 말머리 추가: "[부동산] ..."                       │   │
│  │                                                        │   │
│  │ 2. JSON 직렬화                                          │   │
│  │    {                                                   │   │
│  │      "title": "[부동산] ...",                          │   │
│  │      "content": "...",                                 │   │
│  │      "reference": "https://..."                        │   │
│  │    }                                                   │   │
│  │                                                        │   │
│  │ 3. HTTP 요청 생성                                       │   │
│  │    requests.post()                                     │   │
│  └────────────────────────────────────────────────────────┘   │
│            │                                                   │
│            │ HTTP/1.1 Request                                 │
│            │ POST /api/news HTTP/1.1                          │
│            │ Host: localhost:8080  ← REST 포트                 │
│            │ Content-Type: application/json                   │
│            │ X-API-Key: xxx                                   │
│            │                                                  │
│            │ Body: JSON (Text, ~500 bytes)                    │
│            ↓                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Spring Boot Backend (JAR)                              │   │
│  │ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │   │
│  │                                                        │   │
│  │ 4. REST Controller (NewsController.java)              │   │
│  │    Port: 8080                                          │   │
│  │    ├─ @PostMapping("/api/news")                       │   │
│  │    ├─ API Key 검증 (Header)                            │   │
│  │    └─ @Valid 검증                                      │   │
│  │                                                        │   │
│  │ 5. JSON → Java Object 역직렬화                          │   │
│  │    NewsCreateRequest {                                 │   │
│  │      String title;                                     │   │
│  │      String content;                                   │   │
│  │      String reference;                                 │   │
│  │    }                                                   │   │
│  │                                                        │   │
│  │ 6. NewsService.createNews() [공통 로직]                │   │
│  │    ├─ 제목 중복 체크                                    │   │
│  │    ├─ 출처 중복 체크                                    │   │
│  │    └─ Entity 변환                                      │   │
│  │                                                        │   │
│  │ 7. NewsRepository.save()                               │   │
│  │    └─ JPA → SQL INSERT                                 │   │
│  └────────────────────────────────────────────────────────┘   │
│            │                                                   │
│            │ JDBC                                              │
│            ↓                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ PostgreSQL (외부 서버)                                  │   │
│  │ 43.201.77.236:5432                                     │   │
│  │                                                        │   │
│  │ 8. INSERT INTO news ...                                │   │
│  └────────────────────────────────────────────────────────┘   │
│            │                                                   │
│            │ Success                                           │
│            ↓                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Spring Boot Backend                                    │   │
│  │                                                        │   │
│  │ 9. NewsResponse → JSON 직렬화                           │   │
│  │    {                                                   │   │
│  │      "id": 1,                                          │   │
│  │      "title": "[부동산] ...",                          │   │
│  │      "createdDate": "2026-01-15T..."                   │   │
│  │    }                                                   │   │
│  └────────────────────────────────────────────────────────┘   │
│            │                                                   │
│            │ HTTP/1.1 Response                                │
│            │ 201 Created                                      │
│            │ Content-Type: application/json                   │
│            │ Body: JSON (Text)                                │
│            ↓                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Python Crawler                                         │   │
│  │                                                        │   │
│  │ 10. response.json() 파싱                               │   │
│  │     ✅ 뉴스 등록 성공                                    │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

📊 특징:
  - 포트: 8080 (REST)
  - 프로토콜: HTTP/1.1
  - 데이터: JSON (Text, ~500 bytes)
  - 처리 시간: ~50-100ms
```

#### 2. gRPC 방식
```
┌─────────────────────────────────────────────────────────────────┐
│ Ubuntu Server (Host)                                            │
│                                                                 │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Python Crawler (crawler.py) [수정됨]                   │   │
│  │ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │   │
│  │                                                        │   │
│  │ 1. RSS 수집 & 파싱 [기존과 동일]                        │   │
│  │    ├─ feedparser.parse()                              │   │
│  │    ├─ 키워드 매칭                                       │   │
│  │    └─ 말머리 추가: "[부동산] ..."                       │   │
│  │                                                        │   │
│  │ 2. Proto 객체 생성 [신규!]                              │   │
│  │    ┌─────────────────────────────────────────┐        │   │
│  │    │ news_pb2.py [자동 생성됨]                │        │   │
│  │    │ ← news.proto에서 생성                    │        │   │
│  │    └─────────────────────────────────────────┘        │   │
│  │    request = news_pb2.NewsCreateRequest(              │   │
│  │        title="[부동산] ...",                           │   │
│  │        content="...",                                  │   │
│  │        reference="https://..."                         │   │
│  │    )                                                   │   │
│  │                                                        │   │
│  │ 3. Protobuf Binary 직렬화 [신규!]                       │   │
│  │    Python Object → Binary (~200 bytes, 60% 압축!)     │   │
│  │    ┌──────────────────────────────┐                   │   │
│  │    │ 0x0a 0x1f 0x5b 0xeb ...     │  Binary!          │   │
│  │    └──────────────────────────────┘                   │   │
│  │                                                        │   │
│  │ 4. gRPC 호출 [신규!]                                    │   │
│  │    ┌─────────────────────────────────────────┐        │   │
│  │    │ news_pb2_grpc.py [자동 생성됨]           │        │   │
│  │    │ ← news.proto에서 생성                    │        │   │
│  │    └─────────────────────────────────────────┘        │   │
│  │    stub.CreateNews(request, metadata=...)             │   │
│  └────────────────────────────────────────────────────────┘   │
│            │                                                   │
│            │ HTTP/2 Request (gRPC)                            │
│            │ POST /news.NewsService/CreateNews                │
│            │ Host: localhost:9090  ← gRPC 포트 (다름!)         │
│            │ Content-Type: application/grpc                   │
│            │ Metadata: x-api-key: xxx                         │
│            │                                                  │
│            │ Body: Protobuf Binary (~200 bytes)               │
│            ↓                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Spring Boot Backend (JAR)                              │   │
│  │ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━  │   │
│  │                                                        │   │
│  │ 5. gRPC Server 수신 [신규!]                             │   │
│  │    Port: 9090 (Netty, 별도 서버!)                      │   │
│  │    ┌─────────────────────────────────────────┐        │   │
│  │    │ grpc-spring-boot-starter                │        │   │
│  │    │ ├─ Netty HTTP/2 Server                  │        │   │
│  │    │ └─ Proto 역직렬화                        │        │   │
│  │    └─────────────────────────────────────────┘        │   │
│  │                                                        │   │
│  │ 6. GrpcServerInterceptor.java [신규 파일!]             │   │
│  │    ├─ Metadata에서 x-api-key 추출                      │   │
│  │    ├─ API Key 검증                                     │   │
│  │    └─ 인증 실패 시 UNAUTHENTICATED 반환                 │   │
│  │                                                        │   │
│  │ 7. NewsGrpcService.java [신규 파일!]                   │   │
│  │    ┌─────────────────────────────────────────┐        │   │
│  │    │ NewsServiceGrpc.NewsServiceImplBase     │        │   │
│  │    │ ← news.proto에서 자동 생성됨             │        │   │
│  │    └─────────────────────────────────────────┘        │   │
│  │    @Override                                           │   │
│  │    public void createNews(                             │   │
│  │        NewsProto.NewsCreateRequest request,            │   │
│  │        StreamObserver<Response> responseObserver)      │   │
│  │                                                        │   │
│  │ 8. Proto → Domain 변환 [신규!]                          │   │
│  │    NewsProto.NewsCreateRequest (Proto)                 │   │
│  │      ↓                                                 │   │
│  │    NewsCreateRequest (Java Domain)                     │   │
│  │                                                        │   │
│  │ 9. NewsService.createNews() [공통 로직, 재사용!]        │   │
│  │    ├─ 제목 중복 체크                                    │   │
│  │    ├─ 출처 중복 체크                                    │   │
│  │    └─ Entity 변환                                      │   │
│  │                                                        │   │
│  │ 10. NewsRepository.save()                              │   │
│  │     └─ JPA → SQL INSERT                                │   │
│  └────────────────────────────────────────────────────────┘   │
│            │                                                   │
│            │ JDBC                                              │
│            ↓                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ PostgreSQL (외부 서버)                                  │   │
│  │ 43.201.77.236:5432                                     │   │
│  │                                                        │   │
│  │ 11. INSERT INTO news ...                               │   │
│  └────────────────────────────────────────────────────────┘   │
│            │                                                   │
│            │ Success                                           │
│            ↓                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Spring Boot Backend                                    │   │
│  │                                                        │   │
│  │ 12. Domain → Proto 변환 [신규!]                         │   │
│  │     NewsResponse (Java Domain)                         │   │
│  │       ↓                                                │   │
│  │     NewsProto.NewsResponse (Proto)                     │   │
│  │                                                        │   │
│  │ 13. Protobuf Binary 직렬화 [신규!]                      │   │
│  │     Java Object → Binary (~200 bytes)                  │   │
│  │     ┌──────────────────────────────┐                   │   │
│  │     │ 0x08 0x01 0x12 0x1f ...     │  Binary!          │   │
│  │     └──────────────────────────────┘                   │   │
│  │                                                        │   │
│  │ 14. responseObserver.onNext(response)                  │   │
│  │     responseObserver.onCompleted()                     │   │
│  └────────────────────────────────────────────────────────┘   │
│            │                                                   │
│            │ HTTP/2 Response (gRPC)                           │
│            │ Status: OK (0)                                   │
│            │ Content-Type: application/grpc                   │
│            │ Body: Protobuf Binary                            │
│            ↓                                                  │
│  ┌────────────────────────────────────────────────────────┐   │
│  │ Python Crawler                                         │   │
│  │                                                        │   │
│  │ 15. Protobuf Binary 역직렬화 [신규!]                    │   │
│  │     Binary → NewsResponse Object                       │   │
│  │     response.id, response.title ...                    │   │
│  │     ✅ [gRPC] 뉴스 등록 성공 - ID: 1                     │   │
│  └────────────────────────────────────────────────────────┘   │
│                                                                 │
└─────────────────────────────────────────────────────────────────┘

📊 특징:
  - 포트: 9090 (gRPC, 별도!)
  - 프로토콜: HTTP/2 (멀티플렉싱, 헤더 압축)
  - 데이터: Protobuf Binary (~200 bytes, 40% 감소!)
  - 처리 시간: ~10-30ms (3-5배 빠름!)
```

### Proto 파일의 역할
1. 타입 안정성: 계약(Contract) 보장
2. 자동 코드 생성: Python/Java 코드 자동 생성
3. 효율적 직렬화: Binary로 크기 40% 감소
4. 버전 호환성: 하위 호환성 유지
```
┌───────────────────────────────────────────────────────────┐
│ news.proto (계약서!)                                      │
│ ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━ │
│                                                           │
│ syntax = "proto3";                                        │
│                                                           │
│ message NewsCreateRequest {                               │
│   string title = 1;        ← 필드 번호로 식별!             │
│   string content = 2;      ← JSON보다 작음!               │
│   string reference = 3;                                   │
│ }                                                         │
│                                                           │
│ service NewsService {                                     │
│   rpc CreateNews(NewsCreateRequest)                       │
│       returns (NewsResponse);                             │
│ }                                                         │
└───────────────────────────────────────────────────────────┘
           │                          │
           │ protoc 컴파일             │
           ↓                          ↓
┌─────────────────────┐    ┌─────────────────────┐
│ Python              │    │ Java                │
│ ━━━━━━━━━━━━━━━━━  │    │ ━━━━━━━━━━━━━━━━━  │
│ news_pb2.py         │    │ NewsProto.java      │
│ news_pb2_grpc.py    │    │ NewsServiceGrpc.java│
│                     │    │                     │
│ - 직렬화/역직렬화    │    │ - 직렬화/역직렬화    │
│ - Stub 생성          │    │ - Service Base      │
└─────────────────────┘    └─────────────────────┘
```

---

## 구현 상세
### 프로젝트 디렉토리 구조
```
bugmakers/
├── backend/
│   ├── src/main/
│   │   ├── java/.../apt/
│   │   │   ├── controller/news/
│   │   │   │   └── NewsController.java      # REST API (Frontend용)
│   │   │   ├── grpc/
│   │   │   │   ├── NewsGrpcService.java     # gRPC Service 구현
│   │   │   │   └── GrpcServerInterceptor.java # gRPC 인증
│   │   │   ├── service/news/
│   │   │   │   └── NewsService.java         # 비즈니스 로직 (공통)
│   │   │   ├── repository/
│   │   │   │   └── NewsRepository.java      # JPA Repository
│   │   │   └── domain/news/
│   │   │       ├── News.java                # 엔티티
│   │   │       ├── NewsCreateRequest.java   # DTO
│   │   │       └── NewsResponse.java        # DTO
│   │   └── proto/
│   │       └── news.proto                   # gRPC Proto 정의 (계약서)
│   ├── compose.yaml                         # Docker Compose (Local용)
│   ├── .env.local                           # Local 환경 변수
│   └── .env.dev                             # Dev 환경 변수
│
└── scripts/
    └── news-crawler/
        ├── crawler.py                       # 메인 크롤러 (gRPC 전용)
        ├── generate_proto.py                # Python Proto 생성 스크립트
        ├── config.yaml                      # RSS 출처 & 키워드
        ├── requirements.txt                 # Python 의존성
        ├── protos/                          # Proto 생성 파일 (Git 제외)
        │   ├── __init__.py                  # 자동 생성
        │   ├── news_pb2.py                  # 자동 생성
        │   └── news_pb2_grpc.py             # 자동 생성
        ├── deploy/
        │   └── setup_cron.sh                # Cron 설정 스크립트
        └── .gitignore
        
 ---       
        
  backend/build/generated/source/proto/    ← 자동 생성
    ├─ NewsProto.java
    └─ NewsServiceGrpc.java        
```

---

### 데이터 플로우 (gRPC)
```
1. [RSS Feed]
   ↓
2. [Python Crawler]
   - RSS 파싱 (feedparser)
   - 키워드 매칭
   - 말머리 추가
   ↓
3. [gRPC Call]
   CreateNews(request) → Backend:9090
   Metadata: x-api-key
   Body: Protobuf Binary
   ↓
4. [GrpcServerInterceptor]
   - API Key 검증
   ↓
5. [NewsGrpcService]
   - Proto → Domain 변환
   ↓
6. [NewsService]
   - 중복 체크 (제목, 출처)
   - Entity 변환
   ↓
7. [NewsRepository]
   - JPA save()
   ↓
8. [PostgreSQL]
   - news 테이블에 저장
   ↓
9. [NewsGrpcService]
   - Domain → Proto 변환
   ↓
10. [Response]
    Protobuf Binary → Python
```

---

### 포트 사용
```
Backend (Spring Boot JAR)
├─ 8080: REST API (Tomcat)      ← Frontend용
└─ 9090: gRPC Service (Netty)   ← Crawler용 (별도 서버)
```

---

### gRPC 통신 흐름
```
┌────────────┐                    ┌────────────┐
│  Crawler   │                    │  Backend   │
└─────┬──────┘                    └──────┬─────┘
      │                                  │
      │ 1. CreateNews(request)           │
      │    metadata: x-api-key           │
      ├─────────────────────────────────>│
      │                                  │
      │                                  │ 2. GrpcServerInterceptor
      │                                  │    API Key 검증
      │                                  │
      │                                  │ 3. NewsGrpcService
      │                                  │    Proto → Domain 변환
      │                                  │
      │                                  │ 4. NewsService
      │                                  │    createNews()
      │                                  │
      │                                  │ 5. DB Insert
      │                                  │
      │                                  │ 6. Domain → Proto 변환
      │                                  │
      │ 7. NewsResponse                  │
      │    (Protobuf Binary)             │
      │<─────────────────────────────────┤
      │                                  │
```
**처리 시간**: ~10-30ms (REST 대비 3-5배 빠름)

---

### 시스템 구조
#### Local 환경 (gRPC)
```
┌─────────────────────────────────────┐
│ Docker Container (Crawler)          │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ Python Crawler               │  │
│  │  - grpcio                    │  │
│  │  - Proto Stub                │  │
│  │  - Binary 직렬화              │  │
│  └──────────────────────────────┘  │
│            │ HTTP/2                 │
│            │ gRPC CreateNews()      │
│            ↓                         │
└────────────┼─────────────────────────┘
             │
             ↓ host.docker.internal:9090
┌─────────────────────────────────────┐
│ Backend (IntelliJ/Host)             │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ REST Controller (Port 8080)  │  │
│  │  - Frontend용                 │  │
│  └──────────────────────────────┘  │
│  ┌──────────────────────────────┐  │
│  │ gRPC Server (Port 9090)      │  │
│  │  - NewsGrpcService           │  │
│  │  - GrpcServerInterceptor     │  │
│  │  - Proto 역직렬화             │  │
│  └──────────────────────────────┘  │
│            ↓                         │
│  ┌──────────────────────────────┐  │
│  │ NewsService (공통)           │  │
│  └──────────────────────────────┘  │
│            ↓                         │
│  ┌──────────────────────────────┐  │
│  │ PostgreSQL (Docker)          │  │
│  │ Port: 5432                   │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```

---

#### Dev 환경 (gRPC)
```
┌─────────────────────────────────────┐
│ Ubuntu Server (Host)                │
│                                     │
│  ┌──────────────────────────────┐  │
│  │ Python Crawler (직접 실행)   │  │
│  │  - python3 crawler.py        │  │
│  │  - grpcio                    │  │
│  │  - Proto Stub                │  │
│  └──────────────────────────────┘  │
│            │ HTTP/2                 │
│            │ gRPC CreateNews()      │
│            ↓                         │
│  ┌──────────────────────────────┐  │
│  │ Backend (JAR 실행)           │  │
│  │                              │  │
│  │  ┌────────────────────────┐ │  │
│  │  │ REST (Port 8080)       │ │  │
│  │  └────────────────────────┘ │  │
│  │  ┌────────────────────────┐ │  │
│  │  │ gRPC (Port 9090)       │ │  │
│  │  │  - NewsGrpcService     │ │  │
│  │  └────────────────────────┘ │  │
│  │  ┌────────────────────────┐ │  │
│  │  │ NewsService (공통)     │ │  │
│  │  └────────────────────────┘ │  │
│  └──────────────────────────────┘  │
│            ↓                         │
│  ┌──────────────────────────────┐  │
│  │ PostgreSQL (외부 서버)       │  │
│  └──────────────────────────────┘  │
└─────────────────────────────────────┘
```



