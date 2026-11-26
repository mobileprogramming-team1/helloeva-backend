output "app_ip" {
  description = "VM 고정 외부 IP"
  value       = google_compute_address.app.address
}

output "app_host" {
  description = "HTTPS 접속 주소 (sslip.io)"
  value       = "${replace(google_compute_address.app.address, ".", "-")}.sslip.io"
}

output "cloud_sql_ip" {
  description = "Cloud SQL 공개 IP (앱 DB_URL에 사용)"
  value       = google_sql_database_instance.main.public_ip_address
}

output "bucket_name" {
  description = "음성 파일 버킷"
  value       = google_storage_bucket.audio.name
}
