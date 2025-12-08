# --------------------------------------------------------
# 1. Build Stage
# --------------------------------------------------------
FROM node:20-alpine as builder

WORKDIR /app

# 🛠️ [수정 포인트] 
# 빌드 컨텍스트가 이미 'frontend' 폴더 내부이므로, 경로에서 'frontend/'를 제거합니다.
COPY package.json package-lock.json ./

# 의존성 설치
RUN npm ci

# 소스 코드 복사 (frontend 폴더 내부의 모든 파일이 복사됨)
COPY . .

# 빌드 실행
RUN npm run build

# --------------------------------------------------------
# 2. Production Stage
# --------------------------------------------------------
FROM nginx:alpine

# ⚠️ nginx.conf는 복사하지 않음 (서버 마운트 방식)

# 빌드 결과물(dist)만 Nginx 기본 경로로 복사
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]