#variable.tf

# 1. 리전 (기본값 설정 가능)
variable "aws_region"{
  description = "AWS region"
  type = string
  default = "ap-northeast-2"
}

# 2. 프로젝트 이름 (리소스 이름 지을 때 접두어로 쓰기 위해!)
variable "project_name"{
  description = "프로젝트 이름"
  type = string
  default = "iron-forge"
}

# 3. EC2 스펙 (기본값 없이 강제 입력받게 할 수도 있음)
variable "ec2_type" {
  description = "EC2 인스턴트 타입"
  type = string
  default = "t3.medium"
}

# 4. DB 스펙
variable "db_type" {
  description = "RDS 인스턴스 클래스"
  type = string
  default = "db.t3.small"
}

variable "db_user" {
  description = "DB username"
  type = string
  sensitive = true
}

variable "db_password" {
  description = "DB 마스터 비밀번호"
  type = string
  sensitive = true
}

variable "my_local_ip" {
  description = "나의 로컬 IP"
  type = string
}