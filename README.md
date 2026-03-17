# BookStore

> Spring Boot 기반 e-커머스 서점 서비스  
> **DDD 아키텍처 + 이벤트 기반 비동기 처리 + DevOps 파이프라인**을 직접 설계·구현한 프로젝트

---

## 1. Overview

BookStore는 도서 검색, 장바구니, 주문, 알림까지의 e-커머스 핵심 흐름을 구현한 백엔드 서비스입니다.  
단순한 CRUD를 넘어, 아래 엔지니어링 목표를 중심으로 설계했습니다.

- **DDD Lite 기반 도메인 설계**로 비즈니스 로직의 결합도를 최소화
- **RabbitMQ 기반 이벤트 드리븐 아키텍처**로 주문-알림 간 비동기 분리
- **포트-어댑터 패턴** 적용으로 도메인이 인프라에 의존하지 않는 구조 확보
- **Blue/Green 무중단 배포** + **PLG 스택 관측성** 구축

---

## 2. Architecture

### 2-1. Backend Architecture

```
bookstore/
├── book/                    # 도서 도메인
│   ├── domain/              #   Entity, Repository (Interface)
│   ├── application/         #   Service (비즈니스 로직)
│   └── presentation/        #   Controller, DTO
│
├── cart/                    # 장바구니 도메인
│   ├── domain/
│   ├── presentation/
│   └── ...
│
├── order/                   # 주문 도메인 (핵심)
│   ├── domain/
│   │   ├── Order.java                # Aggregate Root
│   │   ├── OrderItem.java            # Value Object
│   │   ├── OrderEventPublisher.java  # Port (Interface)
│   │   └── event/
│   │       └── OrderMessage.java     # Domain Event
│   ├── application/
│   │   └── OrderService.java         # 주문 유스케이스
│   ├── infra/
│   │   └── messaging/
│   │       └── RabbitMqOrderProducer.java  # Adapter (구현체)
│   └── presentation/
│
├── notification/            # 알림 도메인
│   ├── domain/
│   ├── application/         #   MailService
│   ├── infra/               #   MailHistoryRepository
│   └── interfaces/
│       └── RabbitMqMailConsumer.java  # 메시지 소비자
│
├── user/                    # 사용자 도메인
│   ├── domain/
│   ├── application/         #   UserService, CustomUserDetailsService
│   └── presentation/
│
└── global/                  # 공통 설정
    ├── config/              #   Security, Redis, RabbitMQ Config
    ├── domain/              #   BaseTimeEntity
    └── presentation/        #   Global Controllers
```

### 2-2. 핵심 설계 결정

#### Port-Adapter 패턴으로 도메인-인프라 분리

```
[Order Domain]                          [Infrastructure]
                                        
OrderService                            
  └─▶ OrderEventPublisher (Interface)   ◀── RabbitMqOrderProducer (구현체)
        ↑ 도메인 레이어 (Port)                ↑ 인프라 레이어 (Adapter)
```

- `OrderEventPublisher`는 **도메인 레이어**에 위치한 인터페이스(Port)
- `RabbitMqOrderProducer`는 **인프라 레이어**에서 이를 구현(Adapter)
- **의존성 방향이 항상 도메인을 향함** → 메시징 기술이 바뀌어도 도메인 코드 변경 불필요
- 예: RabbitMQ → Kafka 전환 시, Adapter만 교체하면 됨

#### 이벤트 드리븐 비동기 처리

```
[주문 생성] → OrderService.createOrderFromCart()
                 │
                 ├─ 1. 유저/카트 검증
                 ├─ 2. 재고 차감 (Book.reduceStockQuantity)
                 ├─ 3. Order + OrderItem 생성 (Cascade 저장)
                 ├─ 4. 장바구니 비우기
                 └─ 5. OrderEventPublisher.publish()  ── 비동기 ──▶  RabbitMQ
                                                                        │
                                                                        ▼
                                                          RabbitMqMailConsumer
                                                                        │
                                                                        ▼
                                                              MailService (메일 발송)
```

- 주문 처리와 알림 발송을 **RabbitMQ로 비동기 분리**
- 메일 서버 장애가 주문 트랜잭션에 영향을 주지 않음
- Consumer가 별도로 메시지를 소비하여 메일 발송 + 이력 저장

---

## 3. Tech Stack

| 영역 | 기술 |
|------|------|
| **Backend** | Java, Spring Boot, Spring Security, JPA/Hibernate |
| **Messaging** | RabbitMQ (비동기 이벤트 처리) |
| **Session** | Redis (JSON 직렬화 기반 세션 관리) |
| **DB** | MySQL 8.0, AWS RDS |
| **Cloud & IaC** | AWS (EC2, RDS, S3, CodeDeploy), Terraform |
| **CI/CD** | GitHub Actions, Docker, Docker Compose |
| **배포 전략** | Nginx Blue/Green 무중단 배포 |
| **Monitoring** | Prometheus, Loki, Grafana, Alertmanager (Slack) |
| **부하 테스트** | k6 |

---

## 4. DevOps Pipeline

### 4-1. CI/CD Flow

```
[GitHub Push (main)]
       │
       ▼
[GitHub Actions]
  ├─ Gradle Build
  ├─ Docker Image Build & Push (Docker Hub)
  ├─ appspec.yml + scripts → zip → S3 Upload
  └─ CodeDeploy 배포 트리거
       │
       ▼
[EC2 - CodeDeploy Agent]
  ├─ deploy.sh 실행
  ├─ Docker Pull (최신 이미지)
  ├─ Green 컨테이너 기동
  ├─ Health Check 대기
  ├─ Nginx 설정 스왑 (Blue → Green)
  └─ Blue 컨테이너 정리
```

### 4-2. Blue/Green 무중단 배포

- Nginx 오픈소스 버전의 DNS 캐싱 한계를 **Docker 내부 DNS Resolver**(127.0.0.11)로 해결
- `set $service_url` 동적 변수 할당 + `nginx -s reload`로 서비스 중단 없이 트래픽 전환
- 롤백: Nginx conf 파일을 이전 버전으로 스왑 → 즉시 복구

### 4-3. Observability (PLG Stack)

```
[Spring Boot App]
       │
       ├──── Metrics ────▶ Prometheus ──▶ Grafana Dashboard
       │
       ├──── Logs ───────▶ Loki ────────▶ Grafana Dashboard  
       │
       └──── Alerts ─────▶ Alertmanager ──▶ Slack 알림
```

- **Prometheus**: 시스템/애플리케이션 메트릭 수집
- **Loki**: 컨테이너 로그 중앙 수집
- **Grafana**: 메트릭 + 로그 통합 대시보드
- **Alertmanager**: 임계치 초과 시 Slack 실시간 알림
- **k6**: 부하 테스트로 배포 환경에서 메트릭 수집 정상 동작 검증

---

## 5. Infra as Code (Terraform)

AWS 리소스를 수동이 아닌 코드로 프로비저닝:

```
infra/
├── main.tf                 # Provider 설정
├── ec2.tf                  # EC2 인스턴스
├── rds.tf                  # RDS (MySQL)
├── security_group.tf       # 보안 그룹
├── monitoring.tf           # 모니터링 관련 리소스
├── backend.tf              # Terraform State 관리
├── backend_resources.tf    # S3 + DynamoDB (State Lock)
├── variables.tf            # 변수 정의
└── install_docker_stack.sh # EC2 초기 설정 스크립트
```

---

## 6. Troubleshooting

### Nginx DNS 캐싱 문제
- **문제**: Nginx 오픈소스 버전이 upstream 호스트의 DNS를 시작 시점에만 resolve하여, Blue/Green 전환 시 트래픽이 이전 컨테이너로 계속 전달됨
- **해결**: Docker 내부 DNS Resolver(`127.0.0.11`)를 명시하고 `set $service_url` 변수로 동적 resolve하도록 변경

### Redis 세션 직렬화 에러
- **문제**: Spring Security 세션을 Redis에 저장할 때 Java Native Serialization 사용으로 역직렬화 에러 발생
- **해결**: JSON 기반 직렬화로 전환 (`RedisConfig`에서 `GenericJackson2JsonRedisSerializer` 적용)

### Docker Compose 부팅 순서
- **문제**: MySQL이 완전히 기동되기 전에 Spring Boot가 연결 시도하여 앱 컨테이너 즉시 종료
- **해결**: `healthcheck` + `depends_on.condition: service_healthy` 설정으로 의존성 순서 보장

---

## 7. Running Locally

```bash
git clone https://github.com/KTC-GIT/bookstore-service.git
cd bookstore-service

# Docker Compose로 전체 스택 실행
docker-compose up -d

# 애플리케이션 접속
# http://localhost:8080
```

### 환경 변수
```bash
SPRING_DATASOURCE_URL=jdbc:mysql://db:3306/bookstore
SPRING_DATASOURCE_USERNAME=your_username
SPRING_DATASOURCE_PASSWORD=your_password
SPRING_REDIS_HOST=redis
SPRING_RABBITMQ_HOST=rabbitmq
```

---

## 8. Project Structure

```bash
bookstore-service/
├── src/
│   ├── main/java/com/practice/bookstore/
│   │   ├── book/           # 도서 도메인
│   │   ├── cart/           # 장바구니 도메인
│   │   ├── order/          # 주문 도메인 (DDD 핵심)
│   │   ├── notification/   # 알림 도메인 (RabbitMQ Consumer)
│   │   ├── user/           # 사용자 도메인
│   │   └── global/         # 공통 설정
│   └── test/
├── infra/                  # Terraform IaC
├── nginx/                  # Nginx 설정 (Blue/Green)
├── scripts/                # 배포 스크립트
├── Dockerfile              # Multi-stage 빌드
├── build.gradle
├── appspec.yml             # CodeDeploy 설정
└── README.md
```