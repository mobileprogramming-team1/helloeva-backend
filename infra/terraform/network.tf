resource "google_project_service" "apis" {
  for_each = toset([
    "compute.googleapis.com",
    "sqladmin.googleapis.com",
    "storage.googleapis.com",
    "iam.googleapis.com",
  ])
  service            = each.key
  disable_on_destroy = false
}

resource "google_compute_network" "main" {
  name                    = "helloeva-vpc"
  auto_create_subnetworks = false
  depends_on              = [google_project_service.apis]
}

resource "google_compute_subnetwork" "main" {
  name          = "helloeva-subnet"
  ip_cidr_range = "10.20.0.0/24"
  network       = google_compute_network.main.id
  region        = var.region
}

resource "google_compute_firewall" "allow_ssh" {
  name    = "helloeva-allow-ssh"
  network = google_compute_network.main.name

  allow {
    protocol = "tcp"
    ports    = ["22"]
  }
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["helloeva-vm"]
}

resource "google_compute_firewall" "allow_http_https" {
  name    = "helloeva-allow-http-https"
  network = google_compute_network.main.name

  allow {
    protocol = "tcp"
    ports    = ["80", "443"]
  }
  source_ranges = ["0.0.0.0/0"]
  target_tags   = ["helloeva-vm"]
}
