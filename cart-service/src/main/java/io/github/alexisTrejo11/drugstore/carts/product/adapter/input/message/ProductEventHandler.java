package io.github.alexisTrejo11.drugstore.carts.product.adapter.input.message;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.model.valueobjects.ProductId;
import io.github.alexisTrejo11.drugstore.carts.product.core.application.ProductInsertCommand;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.event.ProductEvent;
import io.github.alexisTrejo11.drugstore.carts.product.core.domain.event.ProductEvent.ProductPayload;
import io.github.alexisTrejo11.drugstore.carts.product.core.port.in.ProductUseCases;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Component
public class ProductEventHandler {
  private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(ProductEventHandler.class);
  private final ProductUseCases productUseCases;

  @Autowired
  public ProductEventHandler(ProductUseCases productUseCases) {
    this.productUseCases = productUseCases;
  }

  @Transactional
  public void handle(ProductEvent event) {
    ProductEvent.ProductPayload payload = event.getPayload();
    if (payload == null || payload.getId() == null) {
      log.warn("Received event with missing payload or id: {}", event);
      return;
    }

    switch (event.getEventType()) {
      case "PRODUCT_CREATED":
        handleCreate(payload);
        break;
      case "PRODUCT_UPDATED":
        handleUpdate(payload);
        break;
      case "PRODUCT_DELETED":
        handleDelete(payload.getId());
        break;
      default:
        log.warn("Unknown event type: {}", event.getEventType());
    }
  }

  private void handleCreate(ProductPayload payload) {
    ProductInsertCommand command = mapToCommand(payload);
    try {
      productUseCases.createProduct(command);
      log.info("Product created with id: {}", payload.getId());
    } catch (Exception e) {
      log.error("Failed to create product {}", payload.getId(), e);
      // Depending on requirements, you might rethrow to trigger retry or DLQ
    }
  }

  private void handleUpdate(ProductPayload payload) {
    ProductInsertCommand command = mapToCommand(payload);
    try {
      productUseCases.updateProduct(command);
      log.info("Product updated with id: {}", payload.getId());
    } catch (Exception e) {
      log.error("Failed to update product {}", payload.getId(), e);
    }
  }

  private void handleDelete(String productId) {
    try {
      productUseCases.deleteProduct(new ProductId(productId));
      log.info("Product deleted with id: {}", productId);
    } catch (Exception e) {
      log.error("Failed to delete product {}", productId, e);
    }
  }

  private ProductInsertCommand mapToCommand(ProductPayload payload) {
    return ProductInsertCommand.builder()
        .id(payload.getId())
        .name(payload.getName())
        .unitPrice(payload.getPrice())
        .description(payload.getDescription())
        .available(payload.isAvailable())
        .discountPerUnit(BigDecimal.ZERO) // default as product-service doesn't provide it
        .build();
  }
}
