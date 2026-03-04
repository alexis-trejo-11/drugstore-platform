#!/bin/bash

echo "Waiting for Kafka to be ready..."
sleep 10

# List of topics to create
TOPICS=(
  "product-events:3:2"
  "order-events:3:2"
  "payment-events:3:2"
  "inventory-events:3:2"
  "user-events:3:2"
  "notification-events:3:2"
  "cart-events:3:2"
  "store-events:3:2"
  "employee-events:3:2"
  "address-events:3:2"
  "auth-events:3:2"
  "admin-events:3:2"
)

# Create topics on each broker
for TOPIC_CONFIG in "${TOPICS[@]}"; do
  IFS=':' read -r TOPIC_NAME PARTITIONS REPLICATION <<< "$TOPIC_CONFIG"

  echo "Creating topic: $TOPIC_NAME with $PARTITIONS partitions and $REPLICATION replication factor"

  docker exec drugstore-kafka-1 kafka-topics --create \
    --if-not-exists \
    --bootstrap-server localhost:9092 \
    --topic "$TOPIC_NAME" \
    --partitions "$PARTITIONS" \
    --replication-factor "$REPLICATION"
done

# List all topics
echo "Listing all topics:"
docker exec drugstore-kafka-1 kafka-topics --list --bootstrap-server localhost:9092

echo "Topics initialized successfully!"