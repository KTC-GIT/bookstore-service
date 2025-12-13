#monitoring.tf

resource "aws_instance" "prometheus_server" {
  ami = "ami-0a71e3eb8b23101ed"   # (기존 ec2.tf에 있는 거랑 똑같은 거 쓰면 됨.
  instance_type = var.ec2_type
  key_name = "iron-forge-kp"   # 키페어 명

  # 2. 보안 그룹 연결
  vpc_security_group_ids = [aws_security_group.monitoring-sg.id]

  # 3. [핵심] 설치 스크립트 심기
  user_data = file("install_docker_stack.sh")

  tags={
    Name = "${var.project_name}-prometheus"
  }
}

# 4. 접속 주소 바로 출력해주기 (편의성)
output "prometheus_url" {
  value = "http://${aws_instance.prometheus_server.public_dns}:9090"
}

output "grafana_url" {
  value = "http://${aws_instance.prometheus_server.public_dns}:3000"
}