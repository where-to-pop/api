# ----------------------
# Gradle 빌드 단계
# ----------------------
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

# 1. 먼저 종속성과 빌드 캐시를 복사
COPY build.gradle.kts settings.gradle.kts gradlew ./
COPY gradle ./gradle

# 2. 의존성만 먼저 다운
RUN ./gradlew dependencies --no-daemon || true

# 3. 이후 실제 소스 복사
COPY src ./src

# 4. 빌드
RUN ./gradlew build --no-daemon -x test


# ----------------------
# 실행 단계
# ----------------------
FROM openjdk:21-bullseye

WORKDIR /app

# 아키텍처 감지
RUN arch=$(dpkg --print-architecture) && \
    echo "Running on architecture: $arch"

# 필수 시스템 패키지 설치 (headless Chromium 구동에 필요)
RUN apt-get update && apt-get install -y \
    wget gnupg unzip curl \
    fonts-liberation libasound2 libatk-bridge2.0-0 libatk1.0-0 libatspi2.0-0 libcups2 \
    libdrm2 libgbm1 libgtk-3-0 libnspr4 libnss3 libwayland-client0 libxcomposite1 \
    libxdamage1 libxfixes3 libxkbcommon0 libxrandr2 libxshmfence1 xdg-utils \
    chromium chromium-driver \
    && rm -rf /var/lib/apt/lists/*

# 크로미움 및 크롬드라이버 버전 확인
RUN chromium --version && chromedriver --version

# 빌드된 JAR 복사
COPY --from=build /app/build/libs/*.jar app.jar

# 셀레니움 실행을 위한 환경변수 설정
ENV JAVA_OPTS="-Dwebdriver.chrome.whitelistedIps= -Dwebdriver.chrome.driver=/usr/bin/chromedriver"
ENV CHROME_BIN=/usr/bin/chromium

# 실행
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
