# ===== STAGE 1: Build =====
FROM eclipse-temurin:21-jdk AS builder

WORKDIR /app

# Gradle 빌드 결과만 사용하므로 jar만 복사
COPY build/libs/*.jar app.jar

# ===== STAGE 2: Runtime =====
FROM eclipse-temurin:21-jre-alpine

# 로컬 타임존 설정 (한국 기준)
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone && \
    apk del tzdata

WORKDIR /app

# 로그 디렉토리 생성 (EC2에서 볼륨 마운트)
RUN mkdir -p /app/logs

# 호스트에서 jar 파일 복사
COPY --from=builder /app/app.jar app.jar

# Spring Profile은 환경변수 주입 (dev / prod 자동 교체)
ENV SPRING_PROFILES_ACTIVE=dev

# JVM 최적화 옵션
ENV JAVA_OPTS="-XX:+UseContainerSupport -XX:MaxRAMPercentage=75.0"

# 애플리케이션 실행
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS -jar app.jar --spring.profiles.active=${SPRING_PROFILES_ACTIVE}"]

# 내부 포트는 항상 8080 (호스트 포트는 Actions에서 결정)
EXPOSE 8080