resource "google_service_account" "app" {
  account_id   = "helloeva-app"
  display_name = "Hello Eva App"
  depends_on   = [google_project_service.apis]
}
