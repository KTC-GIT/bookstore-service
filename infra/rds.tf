#rds.tf

resource "aws_db_instance" "iron-forge-db" {
  # 1. 기본 스펙 (제일 중요)
  identifier = "iron-forge-db"      # 실제 DB 이름 (AWS 콘솔 확인)
  engine = "mysql"
  engine_version = "8.0.43"         # plan 결과에 나온 버전 그대로 적기
  instance_class = var.db_type    # plan 결과 보고 적기
  allocated_storage = 20            # 용량 (GB)
  storage_type = "gp3"              # 혹은 gp2

  # 2. 계정 정보
  username = var.db_user       # 사용자 이름
  password = var.db_password          # plan에는 안나옴 개인이 기억!

  # 3. 네트워크 & 보안
  # [핵심] 하드코딩 말고, 가져온 보안 그룹 참조하기!
  vpc_security_group_ids = [aws_security_group.iron-forge-db-sg.id]

  # 서브넷 그룹 (이건 plan 결과에 db_subnet_group_name 보고 적기)
  db_subnet_group_name = "default-vpc-0a1881c3568e17003"

  # 외부 접속 허용 여부 (plan 결과 확인 : true/false)
  publicly_accessible = false

  # 4. 기타 필수 옵션 (보통 기본값들인데 명시 안하면 자꾸 바꾸려고 함)
  skip_final_snapshot = true      # 삭제할 때 스냅샷 안 찍고 지우기(실습용)
  multi_az = false                # 다중 AZ 아님 (실습용)
  parameter_group_name = "default.mysql8.0"   # plan 보고 적기

  storage_encrypted = true
  max_allocated_storage = 1000
}