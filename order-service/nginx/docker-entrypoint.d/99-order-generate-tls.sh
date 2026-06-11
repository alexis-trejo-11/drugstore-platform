#!/bin/sh
# Dev-only: if mounted TLS files are missing or invalid, generate a self-signed cert
# so Nginx can start without committing nginx.crt/nginx.key to git.

set -e
CERT=/etc/nginx/ssl/nginx.crt
KEY=/etc/nginx/ssl/nginx.key

if openssl x509 -in "$CERT" -noout 2>/dev/null && openssl rsa -in "$KEY" -check -noout 2>/dev/null; then
  exit 0
fi

echo "order-nginx: generating self-signed TLS in /etc/nginx/ssl (dev only)..."
if ! command -v openssl >/dev/null 2>&1; then
  apk add --no-cache openssl
fi
openssl req -x509 -nodes -days 365 \
  -newkey rsa:2048 \
  -keyout "$KEY" \
  -out "$CERT" \
  -subj "/C=US/ST=Dev/L=Local/O=Drugstore/OU=Auth/CN=localhost" \
  -addext "subjectAltName=DNS:localhost,DNS:order-service,IP:127.0.0.1"
chmod 644 "$CERT" 2>/dev/null || true
chmod 600 "$KEY" 2>/dev/null || true
