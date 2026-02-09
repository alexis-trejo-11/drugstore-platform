package microservice.product_service.app.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import microservice.product_service.app.application.port.input.command.UpdateProductCommand;
import microservice.product_service.app.application.port.output.ProductRepository;
import microservice.product_service.app.domain.exception.ProductConflictException;
import microservice.product_service.app.domain.exception.ProductNotFoundException;
import microservice.product_service.app.domain.model.Product;

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
