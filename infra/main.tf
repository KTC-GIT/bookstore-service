#main.tf

terraform {
  required_version = ">= 1.9.0"
  required_providers {
    aws = {
      source = "hashicorp/aws"
      version = "~> 6.25"
    }
  }
}

# 1. 공급자 설정(AWS)
provider "aws" {
  region = var.aws_region
}
/*
# 2. 테스트용 리소스 (S3 버킷)
resource "aws_s3_bucket" "my_tf_test_bucket"{
  #버킷 이름은 고유해야함!
  bucket= "bookstore-tf-test-20251205-my-birth-day"

  tags = {
    Name = "Terraform Test"
    Environment = "Practice"
  }
}
 */