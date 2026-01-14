package microservice.product_service.app.application.mapper;

import java.util.HashSet;
import java.util.Set;

import org.springframework.stereotype.Component;

import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.domain.model.CreateProductParams;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.valueobjects.ActiveIngredient;
import microservice.product_service.app.domain.model.valueobjects.Administration;
import microservice.product_service.app.domain.model.valueobjects.Dosage;
import microservice.product_service.app.domain.model.valueobjects.Manufacturer;
import microservice.product_service.app.domain.model.valueobjects.ProductCode;
import microservice.product_service.app.domain.model.valueobjects.ProductDescription;
import microservice.product_service.app.domain.model.valueobjects.ProductName;
import microservice.product_service.app.domain.model.valueobjects.ProductPrice;
import microservice.product_service.app.domain.model.valueobjects.ProductDates;

@Component
public class ProductMapper {

  public Product createCommandToProduct(CreateProductCommand cmd) {
    Set<String> contraindications = cmd.getContraindications() != null
        ? new HashSet<>(cmd.getContraindications())
        : new HashSet<>();

    Dosage dosage = cmd.isRequiresPrescription()
        ? Dosage.createForPrescription(cmd.getDosage())
        : Dosage.create(cmd.getDosage());

    var params = CreateProductParams.builder()
        .code(ProductCode.NONE)
        .barcode(cmd.getBarcode())
        .name(ProductName.create(cmd.getName()))
        .description(ProductDescription.create(cmd.getDescription()))
        .activeIngredient(ActiveIngredient.create(cmd.getActiveIngredient()))
        .manufacturer(Manufacturer.createRequired(cmd.getManufacturer()))
        .category(cmd.getCategory())
        .price(ProductPrice.create(cmd.getPrice()))
        .dates(ProductDates.create(cmd.getManufactureDate(), cmd.getExpirationDate()))
        .requiresPrescription(cmd.isRequiresPrescription())
        .status(ProductStatus.INACTIVE)
        .contraindications(contraindications)
        .dosage(dosage)
        .administration(Administration.create(cmd.getAdministration()))
        .build();

    return Product.create(params);
  }

  public Product applyUpdate(Product product, UpdateProductCommand cmd) {
    // Update basic info if provided
    if (cmd.getName() != null || cmd.getDescription() != null || cmd.getManufacturer() != null
        || cmd.getCategory() != null) {
      var name = cmd.getName() != null ? ProductName.create(cmd.getName()) : product.getName();
      var description = cmd.getDescription() != null ? ProductDescription.create(cmd.getDescription())
          : product.getDescription();
      var manufacturer = cmd.getManufacturer() != null ? Manufacturer.createRequired(cmd.getManufacturer())
          : product.getManufacturer();
      var category = cmd.getCategory() != null ? cmd.getCategory() : product.getCategory();
      product.updateInformation(name, description, manufacturer, category);
    }

    // Update pricing if provided
    if (cmd.getPrice() != null) {
      product.updatePricing(ProductPrice.create(cmd.getPrice()));
    }

    // Update medical info if any provided
    boolean hasMedicalChanges = true;

    var active = cmd.getActiveIngredient() != null ? ActiveIngredient.create(cmd.getActiveIngredient())
        : product.getActiveIngredient();
    Set<String> contraindications = cmd.getContraindications() != null ? new HashSet<>(cmd.getContraindications())
        : product.getContraindications();
    var dosage = cmd.isRequiresPrescription() ? Dosage.createForPrescription(cmd.getDosage())
        : Dosage.create(cmd.getDosage());
    var admin = cmd.getAdministration() != null ? Administration.create(cmd.getAdministration())
        : product.getAdministration();
    product.updateMedicalInfo(active, contraindications, dosage, admin, cmd.isRequiresPrescription());

    return product;
  }
}
