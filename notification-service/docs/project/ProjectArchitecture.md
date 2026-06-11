# Architecture

## Reverse proxy (Nginx)

This repository does **not** bundle an Nginx edge container. Terminate TLS at your cluster ingress or reuse the `nginx/` + compose pattern from sibling microservices.

