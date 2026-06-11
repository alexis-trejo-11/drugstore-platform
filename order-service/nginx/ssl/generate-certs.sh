#!/bin/sh
# Generates a self-signed TLS certificate for local Nginx development.
# Run this once before `docker compose up`:
#   chmod +x nginx/ssl/generate-certs.sh && ./nginx/ssl/generate-certs.sh

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"

openssl req -x509 -nodes -days 365 \
  -newkey rsa:2048 \
  -keyout "$SCRIPT_DIR/nginx.key" \
  -out    "$SCRIPT_DIR/nginx.crt" \
  -subj   "/C=US/ST=Dev/L=Local/O=Drugstore/OU=Auth/CN=order-service" \
  -addext "subjectAltName=DNS:order-service,DNS:localhost,IP:127.0.0.1"

echo "Certificates generated in $SCRIPT_DIR"
echo "  nginx.key  – private key"
echo "  nginx.crt  – self-signed certificate (valid 365 days)"
