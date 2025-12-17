#security_group.tf

# 일단 껍데기만 선언
resource "aws_security_group" "iron-forge-sg" {
  # 1. 기본 정보
  name = "fire-iron-forge"
  description = "fire-iron-forge"
  vpc_id = "vpc-0a1881c3568e17003"

  # 2. 인바운드 규칙 (ingress) - 들어오는 구멍

  # 22번 포트 (SSH)
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = [var.my_local_ip]
    description = "SSH"
  }

  # 3306 포트 (DB)
  ingress {
    from_port = 3306
    to_port = 3306
    protocol = "tcp"
    cidr_blocks = [var.my_local_ip]
    description = "mysql"
  }

  # HTTP
  ingress {
    from_port = 80
    to_port = 80
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "webService"
  }

  # HTTPS
  ingress {
    from_port = 443
    to_port = 443
    protocol = "tcp"
    cidr_blocks = ["0.0.0.0/0"]
    description = "https"
  }

  ingress{
    from_port = 9100
    to_port = 9100
    protocol = "tcp"
    cidr_blocks = []
    security_groups = [aws_security_group.monitoring-sg.id]
    description = "Node Exporter"
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"   # 모든 프로토콜
    cidr_blocks = ["0.0.0.0/0"]
  }

  tags = {
    Name = "iron-forge-sg"
  }
}

resource "aws_security_group" "iron-forge-db-sg" {
  # 1. 기본 정보
  name = "iron-forge-db-sg"
  description = "RDS Security Group"
  vpc_id = "vpc-0a1881c3568e17003"

  ingress {
    from_port = 3306
    to_port = 3306
    protocol = "tcp"
    cidr_blocks = []
    description = "RDS"
    security_groups = [aws_security_group.iron-forge-sg.id]
  }

  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"
    cidr_blocks = ["0.0.0.0/0"]
  }
}

resource "aws_security_group" "monitoring-sg"{
  name = "monitoring-sg"
  description = "monitoring security group"
  vpc_id = "vpc-0a1881c3568e17003"

  # Grafana 접속용
  ingress{
    from_port = 3000
    to_port = 3000
    protocol = "tcp"
    cidr_blocks = [var.my_local_ip]
    description = "grafana"
  }

  # Prometheus 접속용
  ingress {
    from_port = 9090
    to_port = 9090
    protocol = "tcp"
    cidr_blocks = [var.my_local_ip]
    description = "prometheus"
  }

  # 22번 포트 (SSH)
  ingress {
    from_port = 22
    to_port = 22
    protocol = "tcp"
    cidr_blocks = [var.my_local_ip]
    description = "SSH"
  }

  # alertmanager
  ingress {
    from_port = 9093
    to_port = 9093
    protocol = "tcp"
    cidr_blocks = [var.my_local_ip]
    description = "alertmanager"
  }


  egress {
    from_port = 0
    to_port = 0
    protocol = "-1"   # 모든 프로토콜
    cidr_blocks = ["0.0.0.0/0"]
  }
}