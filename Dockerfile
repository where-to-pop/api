# Gradle 빌드 단계
FROM gradle:8.11.1-jdk17 AS build
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


# 실행 단계
FROM openjdk:17-slim
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app
COPY --from=build /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]