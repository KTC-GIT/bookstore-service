#ec2.tf

resource "aws_instance" "iron-forge" {
  instance_type = var.ec2_type
  ami = "ami-0a71e3eb8b23101ed"
  tags = {
    "Name" = var.project_name
  }
  vpc_security_group_ids = [aws_security_group.iron-forge-sg.id]
}