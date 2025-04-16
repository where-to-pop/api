# Gradle 빌드 단계
FROM gradle:8.11.1-jdk21 AS build
WORKDIR /app

# 프로젝트 소스 복사
COPY . .

RUN ./gradlew build --no-daemon -x test

# 실행 단계
FROM openjdk:21-slim
ARG JAR_FILE=build/libs/*.jar
WORKDIR /app
COPY --from=build /app/${JAR_FILE} app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]