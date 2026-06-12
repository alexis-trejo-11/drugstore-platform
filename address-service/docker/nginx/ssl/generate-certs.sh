#!/bin/sh
# Generates a self-signed TLS certificate for Nginx in local/dev Docker stacks.
# NOT suitable for production — replace with a CA-signed cert or use Let's Encrypt.
#
# Usage (from address-service/docker/nginx/ssl/):
#   chmod +x generate-certs.sh
#   ./generate-certs.sh
#
# Output:
#   nginx.key  — private key
#   nginx.crt  — self-signed certificate (valid 365 days)

set -e

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
KEY="$SCRIPT_DIR/nginx.key"
CRT="$SCRIPT_DIR/nginx.crt"

if [ -d "$KEY" ] || [ -d "$CRT" ]; then
    echo "ERROR: $KEY or $CRT is a directory (usually created by Docker when certs were missing)."
    echo "Stop nginx, then remove the directories and run this script again:"
    echo "  cd <address-service>/docker && docker compose -f docker-compose.full.yml --profile local stop nginx"
    echo "  rm -rf \"$KEY\" \"$CRT\""
    exit 1
fi

if [ -f "$KEY" ] && [ -f "$CRT" ]; then
    echo "Certificates already exist at $SCRIPT_DIR — skipping generation."
    echo "Delete nginx.key and nginx.crt to regenerate."
    exit 0
fi

echo "Generating self-signed certificate for Nginx (dev/local)..."

openssl req -x509 -nodes -days 365 \
    -newkey rsa:2048 \
    -keyout "$KEY" \
    -out    "$CRT" \
    -subj   "/C=MX/ST=Local/L=Local/O=DrugstorePlatform/OU=Dev/CN=address-service" \
    -addext "subjectAltName=DNS:address-service,DNS:localhost,IP:127.0.0.1"

chmod 600 "$KEY"
chmod 644 "$CRT"

echo "Done."
echo "  Key : $KEY"
echo "  Cert: $CRT"
echo ""
echo "These files are mounted into the nginx container by docker-compose.full.yml."
echo "Add nginx.key and nginx.crt to .gitignore to avoid committing secrets."
