# -------------------------------------------------------------
# 1. 빌드 스테이지 (Builder)
# -------------------------------------------------------------
FROM eclipse-temurin:17-jdk-jammy AS builder

WORKDIR /app

# 소스 복사 및 Gradle 실행 권한 부여
COPY gradlew .
COPY gradle gradle
COPY build.gradle settings.gradle ./

# 의존성 캐싱을 위해 빌드 파일 먼저 다운로드 (선택적 최적화)
RUN chmod +x gradlew && ./gradlew dependencies --no-daemon || true

# 전체 소스 복사 후 빌드 (테스트는 CI 단계에서 검증하므로 -x test로 빠르게 패키징)
COPY src src
RUN ./gradlew bootJar --no-daemon -x test

# -------------------------------------------------------------
# 2. 실행 스테이지 (Runtime)
# -------------------------------------------------------------
FROM eclipse-temurin:17-jre-alpine

WORKDIR /app

# 타임존 설정 (한국 시간 KST)
RUN apk add --no-cache tzdata && \
    cp /usr/share/zoneinfo/Asia/Seoul /etc/localtime && \
    echo "Asia/Seoul" > /etc/timezone

# 보안을 위해 비루트(non-root) 사용자 생성 및 권한 설정
RUN addgroup -S appgroup && adduser -S appuser -G appgroup
USER appuser

# 빌드 스테이지에서 생성된 JAR 파일만 복사 (이미지 용량 최소화: 약 200MB 수준)
COPY --from=builder /app/build/libs/*-SNAPSHOT.jar app.jar

# 서비스 포트 오픈
EXPOSE 8080

# 컨테이너 실행 명령어 (JVM 튜닝 및 기본 설정)
ENTRYPOINT ["java", "-Djava.security.egd=file:/dev/./urandom", "-Duser.timezone=Asia/Seoul", "-jar", "app.jar"]
