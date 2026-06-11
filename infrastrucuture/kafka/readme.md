# Kafka Infrastructure for Drugstore Microservices

This directory contains the centralized Kafka setup for all drugstore microservices.

## Services

- **Zookeeper**: Coordination service for Kafka (port 2181)
- **Kafka Brokers**: 3-node Kafka cluster (ports 9092-9095)
- **Kafka UI**: Web UI for managing Kafka (port 8080)
- **Schema Registry**: For Avro schema management (port 8081) - Optional
- **Kafka Connect**: For data integration (port 8083) - Optional

## Getting Started

### Start the Kafka cluster

```bash
cd kafka-infrastructure
docker-compose up -d