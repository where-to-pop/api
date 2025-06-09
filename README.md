# Where To Pop API

팝업 스토어 위치 추천 및 정보 제공 API

## 기술 스택

- Kotlin + Spring Boot 3.3
- MariaDB
- Elasticsearch
- Docker & Docker Compose

## 환경 설정

### 필수 사항

- JDK 21
- Docker & Docker Compose

### 시작하기

1. 저장소 클론:

```bash
git clone https://github.com/your-username/where-to-pop.git
cd where-to-pop-api
```

2. 환경 변수 설정:
   (필요에 따라 `.env` 파일 수정)

3. Docker Compose로 인프라 시작:

```bash
docker-compose up -d
```

4. 애플리케이션 실행:

```bash
./gradlew bootRun
```

## API 문서

API 문서는 애플리케이션 실행 후 다음 URL에서 확인할 수 있습니다:

- Swagger UI: http://localhost:8080/docs

## 개발 도구

- DBGate (DB 관리): http://localhost:3000
- Kibana (Elasticsearch 관리): http://localhost:5601

## 인프라 구성

- MariaDB: `localhost:3306` (또는 환경변수에 설정된 포트)
- Elasticsearch: `localhost:9200`
- Kibana: `localhost:5601`
- DBGate: `localhost:3000`

## CI

이 저장소는 GitHub Actions로 테스트를 자동 실행하도록 설정되어 있습니다. 변경 사항이 `main` 브랜치에 push되거나 Pull Request가 생성되면 워크플로가 실행되어 Gradle 빌드와 테스트를 수행합니다.
