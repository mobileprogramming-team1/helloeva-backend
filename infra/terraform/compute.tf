resource "google_compute_address" "app" {
  name       = "helloeva-app-ip"
  region     = var.region
  depends_on = [google_project_service.apis]
}

resource "google_compute_instance" "app" {
  name                      = "helloeva-app"
  machine_type              = "e2-micro"
  zone                      = var.zone
  tags                      = ["helloeva-vm"]
  allow_stopping_for_update = true

  boot_disk {
    initialize_params {
      image = "ubuntu-os-cloud/ubuntu-2404-lts-amd64"
      size  = 20
      type  = "pd-standard"
    }
  }

  network_interface {
    subnetwork = google_compute_subnetwork.main.id
    access_config {
      nat_ip = google_compute_address.app.address
    }
  }

  service_account {
    email  = google_service_account.app.email
    scopes = ["cloud-platform"]
  }

  metadata = {
    ssh-keys = "${var.ssh_username}:${var.ssh_public_key}"
  }

  metadata_startup_script = file("${path.module}/startup.sh")

  depends_on = [google_project_service.apis]
}
