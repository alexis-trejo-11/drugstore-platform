package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.products.core.port.input.command.UpdateProductCommand;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductEventPublisher;
import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductConflictException;
import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductNotFoundException;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateProductUseCase {
  private final ProductRepository repository;
  private final ProductEventPublisher eventPublisher;

  @Transactional
  public Product updateProduct(UpdateProductCommand command) {
    log.info("Updating product with ID: {}", command.productId());

    Product existingProduct = repository.findByID(command.productId())
        .orElseThrow(() -> new ProductNotFoundException(command.productId()));

    validateUniqueness(command);
    Product productUpdated = applyUpdate(existingProduct, command);

    Product savedProduct = repository.save(productUpdated);
    log.info("Product updated successfully with ID: {}", savedProduct.getId());

    publishEvent(() -> eventPublisher.publishProductUpdated(savedProduct),
                 "Product updated", savedProduct.getId());

    return savedProduct;
  }

  private Product applyUpdate(Product product, UpdateProductCommand cmd) {
    if (cmd.hasAnyBasicInfoUpdates()) {
      product.updateInformation(
          cmd.name(),
          cmd.barcode(),
          cmd.sku(),
          cmd.description(),
          cmd.manufacturer(),
          cmd.classification(),
          cmd.images());
    }

    if (cmd.hasPricingUpdate()) {
      product.updatePricing(cmd.price());
    }

    if (cmd.hasAnyMedicalInfoUpdates()) {
      product.updateMedicalInfo(
          cmd.activeIngredient(),
          cmd.contraindications(),
          cmd.dosage(),
          cmd.administration(),
          cmd.requiresPrescription());
    }

    return product;
  }

  private void validateUniqueness(UpdateProductCommand cmd) {
    if (cmd.hasSkuUpdate() && repository.existsBySKU(cmd.sku())) {
      throw new ProductConflictException("Product code must be unique");
    }

    if (cmd.hasBarcodeUpdate() && repository.existsByBarCode(cmd.barcode())) {
      throw new ProductConflictException("Product barcode must be unique");
    }
  }

  private void publishEvent(Runnable eventPublisher, String eventType, Object productId) {
    try {
      eventPublisher.run();
      log.debug("{} event published for product ID: {}", eventType, productId);
    } catch (Exception e) {
      log.error("Failed to publish {} event for product ID: {}, but product was updated. Error: {}",
               eventType, productId, e.getMessage(), e);
    }
  }
}
