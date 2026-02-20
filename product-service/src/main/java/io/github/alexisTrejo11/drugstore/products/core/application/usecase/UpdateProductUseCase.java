package io.github.alexisTrejo11.drugstore.products.core.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.products.core.port.input.command.UpdateProductCommand;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductConflictException;
import io.github.alexisTrejo11.drugstore.products.core.domain.exception.ProductNotFoundException;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;

@Service
public class UpdateProductUseCase {
  private final ProductRepository repository;

  @Autowired
  public UpdateProductUseCase(ProductRepository repository) {
    this.repository = repository;
  }

  public Product updateProduct(UpdateProductCommand command) {
    Product existingProduct = repository.findByID(command.productId())
        .orElseThrow(() -> new ProductNotFoundException(command.productId()));

    validateUniqueness(command);
    Product productUpdated = applyUpdate(existingProduct, command);

    return repository.save(productUpdated);
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
}
