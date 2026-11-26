#!/bin/bash
set -e

APP_HOST="$(curl -s -H 'Metadata-Flavor: Google' \
  http://metadata.google.internal/computeMetadata/v1/instance/network-interfaces/0/access-configs/0/external-ip \
  | tr '.' '-').sslip.io"

sudo apt-get update -y
sudo apt-get install -y nginx certbot python3-certbot-nginx

sed "s/__APP_HOST__/${APP_HOST}/" "$(dirname "$0")/helloeva.conf" | sudo tee /etc/nginx/sites-available/helloeva > /dev/null
sudo ln -sf /etc/nginx/sites-available/helloeva /etc/nginx/sites-enabled/helloeva
sudo rm -f /etc/nginx/sites-enabled/default
sudo nginx -t
sudo systemctl reload nginx

sudo certbot --nginx -d "${APP_HOST}" --non-interactive --agree-tos --register-unsafely-without-email --redirect

echo "https://${APP_HOST}"
