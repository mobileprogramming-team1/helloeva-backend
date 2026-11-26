resource "google_storage_bucket" "audio" {
  name          = var.bucket_name
  location      = upper(var.region)
  storage_class = "STANDARD"
  force_destroy = true

  uniform_bucket_level_access = true

  depends_on = [google_project_service.apis]
}

resource "google_storage_bucket_iam_member" "public_read" {
  bucket = google_storage_bucket.audio.name
  role   = "roles/storage.objectViewer"
  member = "allUsers"
}

resource "google_storage_bucket_iam_member" "app_write" {
  bucket = google_storage_bucket.audio.name
  role   = "roles/storage.objectAdmin"
  member = "serviceAccount:${google_service_account.app.email}"
}
