variable "project_id" {
  description = "GCP 프로젝트 ID"
  type        = string
  default     = "helloeva-a5e901"
}

variable "region" {
  description = "GCP 리전 (서울)"
  type        = string
  default     = "asia-northeast3"
}

variable "zone" {
  description = "GCP 존"
  type        = string
  default     = "asia-northeast3-a"
}

variable "bucket_name" {
  description = "음성 파일 GCS 버킷 이름 (전 세계 유일)"
  type        = string
  default     = "helloeva-a5e901-audio"
}

variable "db_name" {
  description = "Cloud SQL 데이터베이스 이름"
  type        = string
  default     = "helloeva"
}

variable "db_user" {
  description = "Cloud SQL 사용자 이름"
  type        = string
  default     = "helloeva"
}

variable "db_password" {
  description = "Cloud SQL 사용자 비밀번호"
  type        = string
  sensitive   = true
}

variable "ssh_username" {
  description = "VM SSH 접속 유저명"
  type        = string
}

variable "ssh_public_key" {
  description = "VM에 등록할 SSH 공개키"
  type        = string
}
