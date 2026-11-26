resource "google_service_account" "app" {
  account_id   = "helloeva-app"
  display_name = "Hello Eva App"
  depends_on   = [google_project_service.apis]
}

resource "google_project_iam_member" "app_roles" {
  for_each = toset([
    "roles/aiplatform.user",
    "roles/serviceusage.serviceUsageConsumer",
  ])
  project = var.project_id
  role    = each.key
  member  = "serviceAccount:${google_service_account.app.email}"
}
