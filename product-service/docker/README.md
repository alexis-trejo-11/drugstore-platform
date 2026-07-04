# Product Service — Docker

See **[docs/docker-local-dev.md](../../docs/docker-local-dev.md)** for shared workflows, profiles, and port reference.

**Service-specific:** bundled Postgres (`product_db`), Redis. Kafka. Port `PRODUCT_SERVICE_PORT` (default **8088**).

```bash
cd product-service
cp .env.example .env
docker compose -f docker/docker-compose.yml --env-file .env up -d --build
```

Full run options are documented in the header of [`docker-compose.yml`](./docker-compose.yml).
