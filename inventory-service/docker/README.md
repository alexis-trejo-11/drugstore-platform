# Inventory Service — Docker

See **[docs/docker-local-dev.md](../../docs/docker-local-dev.md)** for shared workflows, profiles, and port reference.

**Service-specific:** bundled Postgres, Redis. Port `INVENTORY_SERVICE_PORT` (default **8084**).

```bash
cd inventory-service
cp .env.example .env
docker compose -f docker/docker-compose.yml --env-file .env up -d --build
```

Full run options are documented in the header of [`docker-compose.yml`](./docker-compose.yml).
