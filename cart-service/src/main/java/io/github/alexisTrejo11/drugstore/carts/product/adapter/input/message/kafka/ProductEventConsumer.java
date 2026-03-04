package io.github.alexisTrejo11.drugstore.carts.product.adapter.input.message.kafka;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.carts.product.adapter.input.message.ProductEventHandler;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.event.ProductEvent;

@Component
public class ProductEventConsumer {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductEventConsumer.class);

  private final ProductEventHandler eventHandler;

  @Autowired
  public ProductEventConsumer(ProductEventHandler eventHandler) {
    this.eventHandler = eventHandler;
  }

  @KafkaListener(topics = "product-events", groupId = "${spring.kafka.consumer.group-id}")
  public void consume(ProductEvent event) {
    log.info("Received product event: type={}, id={}", event.getEventType(), event.getPayload().getId());
    eventHandler.handle(event);
  }
}