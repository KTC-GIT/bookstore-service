#backend_resources.tf

# 1. 세이바 파일을 저장할 S3 버킷
resource "aws_s3_bucket" "tf_state" {
  # 버킷 이름은 전세계 고유해야함.
  bucket = "iron-forge-tf-state-ktc"

  tags = {
    Name = "Terraform State Storage"
  }
}

# 2. 버킷 버전 관리 켜기 (실수해도 되돌릴 수 있도록!)
resource "aws_s3_bucket_versioning" "tf_state_ver" {
  bucket = aws_s3_bucket.tf_state.id
  versioning_configuration {
    status = "Enabled"
  }
}

# 3. 동시에 수정 못 하게 막는 자물쇠 (DynamoDB)
resource "aws_dynamodb_table" "tf_lock" {
  name = "iron-forge-tf-lock"
  billing_mode = "PAY_PER_REQUEST"  # 쓴 만큼 내기 (거의 공짜)
  hash_key = "LockID"

  attribute {
    name = "LockID"
    type = "S"
  }

  tags = {
    Name = "Terraform Lock Table"
  }
}