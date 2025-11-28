# 전자서점 프로젝트 (Spring Boot + DevOps)

## 1. 한 줄 소개

Spring Boot 기반 전자서점 서비스에  
**Docker, GitHub Actions, AWS(CodeDeploy, EC2)**를 붙여서  
“코드 푸시 → 도커 이미지 빌드/푸시 → S3 업로드 → CodeDeploy → EC2 배포”까지  
**엔드투엔드 CI/CD 파이프라인을 구축한 개인 프로젝트입니다.

---

## 2. 프로젝트 목표

- 레거시 환경이 아닌 **클라우드/컨테이너 기반 환경에서의 실전 경험** 쌓기
- DevOps 전환을 위해:
    - CI/CD
    - 무중단 배포(Blue-Green, Nginx)
    - 관측 도구(Prometheus, Grafana, k6 등 향후 추가)
      를 실제로 손으로 구현해보기

---

## 3. 기능 개요 (애플리케이션)

- 도서 조회
    - 목록 / 상세 페이지 (Thymeleaf + Bootstrap)
- 장바구니
    - 장바구니 담기 / 수정 / 삭제
- 주문
    - 장바구니 → 주문 생성
    - 주문 목록 + 주문 상세 조회
- 인증/사용자
    - Spring Security 기반 로그인 / 회원가입
    - 향후: 로그인 유저 기준 장바구니/주문 연결

---

## 4. 아키텍처 & 인프라

### 4-1. 전체 구조

- **로컬/EC2 공통**
    - `Spring Boot API` (전자서점)
    - `MySQL` (Docker 컨테이너)
- **운영 환경 (AWS EC2)**
    - EC2 (Ubuntu 24.04, t3.medium)
    - Docker + Docker Compose
    - Nginx (리버스 프록시, Blue-Green 무중단 배포)
    - GitHub Actions → Docker Hub → S3 → CodeDeploy → EC2

(간단한 구조 다이어그램 넣고 싶으면 나중에 추가)

---

## 5. DevOps 구성 (핵심 포인트)

### 5-1. CI/CD 파이프라인

- 트리거: `main` 브랜치 push
- GitHub Actions Job:
    1. Gradle 빌드 (`gradle clean build -x test`)
    2. Docker 이미지 빌드 & Docker Hub 푸시
        - 레포: `crazybeerdwarf/bookstore:latest`
    3. `appspec.yml + scripts`를 묶어 `bookstore-deploy.zip` 생성
    4. S3 버킷(`iron-forge-bucket`)에 업로드
    5. CodeDeploy 배포 생성
        - 애플리케이션: `iron-forge-bookstore`
        - 배포 그룹: `iron-forge-clan`

### 5-2. 배포(EC2) 구조

- EC2 인스턴스: `iron-forge`
- Docker Compose:
    - `db`: MySQL 8.0, `local-db-data` 볼륨 사용
    - `app_blue`, `app_green`: Spring Boot 컨테이너 (Blue/Green)
    - `nginx`: 리버스 프록시 + 무중단 배포 스위칭 담당

### 5-3. Blue-Green 무중단 배포

- `app_blue` = 현재 운영 버전
- `app_green` = 새 버전 후보
- 신규 배포 시:
    1. GitHub Actions가 새 이미지 푸시
    2. CodeDeploy가 EC2에서 `app_green` 기동
    3. 헬스 체크 후 Nginx 설정을 `blue → green`으로 변경
    4. 안정화 후 `app_blue` 종료 (롤백 시 다시 `blue`로 스위치)

(※ 현재는 수동/스크립트 기반 스위칭 → 향후 자동화 예정)

---

## 6. 사용 기술 스택

- Backend
    - Java, Spring Boot, Spring Security, Spring Batch
- DB
    - MySQL 8.0 (Docker)
- Infra / DevOps
    - Docker, Docker Compose
    - GitHub Actions
    - AWS EC2, S3, CodeDeploy
    - Nginx (Reverse Proxy, Blue-Green)
    - (계획) Prometheus, Grafana, k6, Elasticsearch, Terraform, Kubernetes

---

## 7. 트러블슈팅 & 배운 점

- **DB가 느리게 기동되어 앱 부팅 실패 문제**
    - 증상: `docker compose up -d` 시 앱 컨테이너가 바로 종료
    - 원인: MySQL이 완전히 기동되기 전에 Spring Boot가 연결 시도
    - 해결:
        - `db` 서비스에 `healthcheck` 추가
        - `depends_on.condition: service_healthy`로 변경

- **GitHub Actions에서 `./gradlew` 실행 문제**
    - 해결: `gradle/actions/setup-gradle` + `gradle clean build -x test`로 대체

- **SSH 직접 접속 배포 시도 실패**
    - 보안 그룹, SSH 키 관리 이슈 → 유지보수 부담
    - 최종적으로 **CodeDeploy + S3** 기반 배포 플로우로 전환

- (추가 예정)
    - Nginx Blue-Green 스위칭 과정 정리
    - 관측 도구 도입 후 메트릭/대시보드 캡쳐

---

## 8. 향후 계획

- Nginx 기반 Blue-Green 배포 자동화 고도화
- Prometheus + Grafana 도입 → 요청/에러/DB 커넥션 모니터링
- k6 부하테스트로 성능/병목 구간 검증
- Elasticsearch 기반 도서 검색 기능
- Terraform / Kubernetes로 인프라 코드화 및 오케스트레이션 실습
