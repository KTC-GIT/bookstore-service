# Bookstore Application

Spring Boot로 구축된 연습 및 포트폴리오용 온라인 서점 프로젝트입니다.

## 개요

이 애플리케이션은 사용자가 책을 둘러보고, 장바구니를 관리하며, 주문을 할 수 있는 간단한 온라인 서점입니다. 사용자 인증 기능을 포함하고 있으며, 전형적인 이커머스 흐름을 보여주도록 설계되었습니다.

## 기술 스택

-   **Language**: Java 21
-   **Framework**: Spring Boot
-   **Database**: MySQL
-   **ORM**: Spring Data JPA
-   **Security**: Spring Security
-   **Template Engine**: Thymeleaf
-   **Build Tool**: Gradle

## 주요 기능

-   **회원 관리 (User Management)**:
    -   회원 가입 (`/join`)
    -   로그인 (`/login`)
-   **도서 목록 (Book Catalog)**:
    -   전체 도서 조회
    -   도서 상세 조회
-   **장바구니 (Shopping Cart)**:
    -   장바구니에 도서 담기
    -   장바구니 목록 조회
-   **주문 시스템 (Order System)**:
    -   장바구니에서 주문 생성
    -   주문 내역 조회

## 시작하기

### 사전 요구 사항

-   Java 21 이상
-   MySQL Server
-   Docker (선택 사항, 컨테이너 배포 시 필요)

### 설정 (Configuration)

데이터베이스 설정이 필요합니다. `src/main/resources/application.properties` (또는 `.yml`) 파일을 생성하거나 수정하여 MySQL 접속 정보를 입력하세요:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/bookstore
spring.datasource.username=YOUR_USERNAME
spring.datasource.password=YOUR_PASSWORD
spring.jpa.hibernate.ddl-auto=update
```

### 프로젝트 빌드

Gradle 래퍼(Wrapper)를 사용하여 프로젝트를 빌드합니다:

```bash
# Linux/macOS
./gradlew build

# Windows
gradlew.bat build
```

### 애플리케이션 실행

빌드가 완료되면 생성된 JAR 파일을 사용하여 애플리케이션을 실행할 수 있습니다:

```bash
java -jar build/libs/bookstore-api.jar
```

애플리케이션은 기본적으로 `http://localhost:8080`에서 시작됩니다.

### Docker로 실행

애플리케이션을 Docker 컨테이너로 빌드하고 실행할 수도 있습니다.

1.  **Docker 이미지 빌드**:

    ```bash
    docker build -t bookstore .
    ```

2.  **컨테이너 실행**:

    ```bash
    docker run -p 8080:8080 bookstore
    ```

    *참고: Dockerfile은 `docker` Spring 프로필(`--spring.profiles.active=docker`)을 사용합니다. 필요한 경우 해당 프로필에 맞는 설정이 있는지 확인하세요.*

## 프로젝트 구조

```
src/main/java/com/practice/bookstore
├── book          # 도서 도메인 (Service, Repository, Controller)
├── cart          # 장바구니 도메인
├── global        # 전역 설정 (Security 등)
├── order         # 주문 도메인
├── user          # 회원 도메인
└── BookstoreApplication.java  # 메인 진입점
```