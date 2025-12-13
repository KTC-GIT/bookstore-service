#!/bin/bash
# install_docker_stack.sh

# --- 1. 기본 패키지 업데이트 및 Docker 필수패키지 설치 (GPT 피드백 반영 : enable 추가) ---
sudo apt-get update -y
sudo apt-get install -y ca-certificates curl gnupg lsb-release

# 2. Docker 공식 GPG 키 추가 및 레포지토리 설정
sudo mkdir -p /etc/apt/keyrings
curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo gpg --dearmor -o /etc/apt/keyrings/docker.gpg
echo \
  "deb [arch=$(dpkg --print-architecture) signed-by=/etc/apt/keyrings/docker.gpg] https://download.docker.com/linux/ubuntu \
  $(lsb_release -cs) stable" | sudo tee /etc/apt/sources.list.d/docker.list > /dev/null

# 3. Docker Engine 설치
sudo apt-get update -y
sudo apt-get install -y docker-ce docker-ce-cli containerd.io docker-compose-plugin

# 4. Docker 자동 실행 설정 및 권한 부여
sudo systemctl enable docker # [중요] 재부팅 해도 Docker 자동 실행
sudo systemctl start docker
sudo usermode -aG docker ubuntu


# --- 3. 작업 디렉토리 및 데이터 볼륨 폴더 생성 (GPT 피드백 반영 : 영속성) ---
mkdir -p /home/ubuntu/monitoring
mkdir -p /home/ubuntu/monitoring/prometheus-data
mkdir -p /home/ubuntu/monitoring/grafana-data

# 권한 설정(Docker 컨테이너가 쓸 수 있게 777로 연다/ 실무에선 UID 맞추지만 실습에선 이게 낫다.)
chmod -R 777 /home/ubuntu/monitoring

cd /home/ubuntu/monitoring

# --- 4. Prometheus 설정 파일 생성 ---
# (localhost:9100은 'host' 모드 덕분에 EC2 자체를 가리키게 됨)
cat <<EOF > prometheus.yml
global:
  scrape_interval: 15s

scrape_configs:
  - job_name: 'prometheus'
    static_configs:
      - targets: ['localhost:9090']
  - job_name: 'node_exporter'
    static_configs:
      - targets: ['localhost:9100']
EOF

# --- 5. docker-compose.yml 생성 (GPT 피드백 반영: volumes, host network) ---
cat <<EOF > docker-compose.yml
version: '3.8'

services:
  prometheus:
    image: prom/prometheus:latest
    container_name: prometheus
    network_mode: host      # [핵심] 호스트 네트워크 사용 (포트 매핑 불필요)
    volumes:
      - ./prometheus.yml:/etc/prometheus/prometheus.yml
      - ./prometheus-data:/prometheus   # [중요] 데이터 사라지지 않게 저장!
    command:
      - '--config.file=/etc/prometheus/prometheus.yml'
      - '--storage.tsdb.path=/prometheus'
    restart: unless-stopped   # [중요] 죽으면 다시 살아나라.

  grafana:
    image: grafana/grafana:latest
    container_name: grafana
    network_mode: host      # [핵심] 호스트 네트워크 (3000번 포트 바로 열림)
    volumes:
      - ./grafana-data:/var/lib/grafana   # [중요] 대시보드 설정 저장
    environment:
      - GF_SECURITY_ADMIN_PASSWORD=admin
    restart: unless-stopped

  node_exporter:
    image: prom/node-exporter:latest
    container_name: node-exporter
    network_mode: host      # [핵심] EC2 호스트의 진짜 리소스를 긁기 위함
    pid: host               # 호스트의 프로세스 정보 접근
    volumes:
      - '/:/host:ro,rslave' # 호스트 파일시스템 마운트 (디스크 용량 체크용)
    command:
      - '--path.rootfs=/host'
    restart: unless-stopped
EOF

# --- 6. 실행 ---
# EC2 기동 시점에 바로 실행되도록 함
docker compose up -d