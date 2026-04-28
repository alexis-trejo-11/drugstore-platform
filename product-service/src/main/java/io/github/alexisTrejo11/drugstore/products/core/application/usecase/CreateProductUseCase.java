package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.products.core.port.input.command.CreateProductCommand;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductEventPublisher;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.CreateProductParams;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class CreateProductUseCase {
  private final ProductRepository repository;
  private final ProductEventPublisher eventPublisher;

  @Transactional
  public Product createProduct(CreateProductCommand command) {
    log.info("Creating new product with SKU: {}", command.sku());

    CreateProductParams params = command.toCreateParams();
    Product newProduct = Product.create(params);

    Product savedProduct = repository.save(newProduct);
    log.info("Product created successfully with ID: {}", savedProduct.getId());

    publishEvent(() -> eventPublisher.publishProductCreated(savedProduct),
                 "Product created", savedProduct.getId());

    return savedProduct;
  }

  private void publishEvent(Runnable eventPublisher, String eventType, Object productId) {
    try {
      eventPublisher.run();
      log.debug("{} event published for product ID: {}", eventType, productId);
    } catch (Exception e) {
      log.error("Failed to publish {} event for product ID: {}, but product was saved. Error: {}",
               eventType, productId, e.getMessage(), e);
    }
  }
}
