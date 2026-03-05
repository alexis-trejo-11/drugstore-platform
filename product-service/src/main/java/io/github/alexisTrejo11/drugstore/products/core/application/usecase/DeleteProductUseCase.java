package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductEventPublisher;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductNotFoundException;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class DeleteProductUseCase {
  private final ProductRepository repository;
  private final ProductEventPublisher eventPublisher;

  @Transactional
  public void deleteProduct(ProductID productId) {
    log.info("Deleting product with ID: {}", productId);

    Product product = repository.findByID(productId)
        .orElseThrow(() -> new ProductNotFoundException(productId));

    repository.deleteByID(productId);
    log.info("Product deleted successfully with ID: {}", productId);

    publishEvent(() -> eventPublisher.publishProductDeleted(productId.toString(), product),
                 "Product deleted", productId);
  }

  private void publishEvent(Runnable eventPublisher, String eventType, Object productId) {
    try {
      eventPublisher.run();
      log.debug("{} event published for product ID: {}", eventType, productId);
    } catch (Exception e) {
      log.error("Failed to publish {} event for product ID: {}, but product was deleted. Error: {}",
               eventType, productId, e.getMessage(), e);
    }
  }
}
