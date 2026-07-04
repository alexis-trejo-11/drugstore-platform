# Notification Service — Docker

See **[docs/docker-local-dev.md](../../docs/docker-local-dev.md)** for shared workflows, profiles, and port reference.

**Service-specific:** bundled MongoDB (`notification_db`). Kafka. Port `NOTIFICATION_SERVICE_PORT` (default **8093**).

```bash
cd notification-service
cp .env.example .env
docker compose -f docker/docker-compose.yml --env-file .env up -d --build
```

Full run options are documented in the header of [`docker-compose.yml`](./docker-compose.yml).
