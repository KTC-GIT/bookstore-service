#backend.tf

terraform {
  backend "s3" {
    # 1. 방금 만든 S3 버킷 이름
    bucket = "iron-forge-tf-state-ktc"

    # 2. 저장될 파일 경로 (폴더/파일이름)
    key = "terraform.tfstate"

    # 3. 리전
    region = "ap-northeast-2"

    # 4. 암호화 설정 (필수)
    encrypt = true

    # 5. 방금 만든 DynamoDB 테이블 이름
    dynamodb_table = "iron-forge-tf-lock"
  }
}