resource "google_sql_database_instance" "main" {
  name             = "helloeva-mysql"
  database_version = "MYSQL_8_4"
  region           = var.region

  settings {
    edition           = "ENTERPRISE"
    tier              = "db-f1-micro"
    availability_type = "ZONAL"
    disk_type         = "PD_HDD"
    disk_size         = 10
    disk_autoresize   = false

    ip_configuration {
      ipv4_enabled = true
      authorized_networks {
        name  = "app-vm"
        value = google_compute_address.app.address
      }
    }

    backup_configuration {
      enabled = false
    }
  }

  deletion_protection = false

  depends_on = [google_project_service.apis]
}

resource "google_sql_database" "app" {
  name     = var.db_name
  instance = google_sql_database_instance.main.name
}

resource "google_sql_user" "app" {
  name     = var.db_user
  instance = google_sql_database_instance.main.name
  password = var.db_password
}
