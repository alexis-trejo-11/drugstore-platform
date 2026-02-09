package microservice.product_service.app.application.port.input.command;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductSubcategory;
import microservice.product_service.app.domain.model.enums.ProductType;
import microservice.product_service.app.domain.model.valueobjects.ActiveIngredient;
import microservice.product_service.app.domain.model.valueobjects.Administration;
import microservice.product_service.app.domain.model.valueobjects.Dosage;
import microservice.product_service.app.domain.model.valueobjects.ExpirationRange;
import microservice.product_service.app.domain.model.valueobjects.Manufacturer;
import microservice.product_service.app.domain.model.valueobjects.ProductClassification;
import microservice.product_service.app.domain.model.valueobjects.ProductDescription;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.domain.model.valueobjects.ProductImages;
import microservice.product_service.app.domain.model.valueobjects.ProductName;
import microservice.product_service.app.domain.model.valueobjects.ProductPrice;
import microservice.product_service.app.domain.model.valueobjects.SKU;

public record UpdateProductCommand(
    ProductID productId,
    ProductName name,
    SKU sku,
    ProductDescription description,
    ActiveIngredient activeIngredient,
    Manufacturer manufacturer,
    ProductClassification classification,
    ProductPrice price,
    String barcode,
    boolean requiresPrescription,
    List<String> contraindications,
    ProductImages images,
    Dosage dosage,
    Administration administration,
    ExpirationRange expirationRange) {
  public boolean hasAnyBasicInfoUpdates() {
    return name() != null || description() != null || manufacturer() != null || classification() != null
        || barcode() != null || images() != null || sku() != null;
  }

  public boolean hasPricingUpdate() {
    return price() != null;
  }

  public boolean hasAnyMedicalInfoUpdates() {
    return activeIngredient() != null || contraindications() != null || dosage() != null || administration() != null
        || requiresPrescription();
  }

  public boolean hasBarcodeUpdate() {
    return barcode() != null;
  }

  public boolean hasSkuUpdate() {
    return sku() != null;
  }

  public static UpdateProductCommandBuilder builder() {
    return new UpdateProductCommandBuilder();
  }

  public static class UpdateProductCommandBuilder {
    ProductID productId;
    ProductName name;
    ProductDescription description;
    ActiveIngredient activeIngredient;
    Manufacturer manufacturer;
    ProductClassification classification;
    ProductPrice price;
    String barcode;
    SKU sku;
    boolean requiresPrescription;
    List<String> contraindications;
    Dosage dosage;
    Administration administration;
    ProductImages images;
    ExpirationRange expirationRange;

    public UpdateProductCommandBuilder productId(String productId) {
      if (productId != null) {
        this.productId = ProductID.from(productId);
      }
      return this;
    }

    public UpdateProductCommandBuilder name(String name) {
      if (name != null) {
        this.name = ProductName.create(name);
      }
      return this;
    }

    public UpdateProductCommandBuilder description(String description) {
      if (description != null) {
        this.description = ProductDescription.create(description);
      }
      return this;
    }

    public UpdateProductCommandBuilder activeIngredient(String ai) {
      if (ai != null) {
        this.activeIngredient = ActiveIngredient.create(ai);
      }
      return this;
    }

    public UpdateProductCommandBuilder manufacturer(String manufacturer) {
      if (manufacturer != null) {
        this.manufacturer = Manufacturer.create(manufacturer);
      }
      return this;
    }

    public UpdateProductCommandBuilder classification(ProductType type, ProductCategory category,
        ProductSubcategory subcategory) {
      if (type != null && category != null) {
        this.classification = ProductClassification.create(type, category,
            subcategory != null ? subcategory : ProductSubcategory.NOT_SPECIFIED);
      }
      return this;
    }

    public UpdateProductCommandBuilder price(BigDecimal price) {
      if (price != null) {
        this.price = ProductPrice.create(price);
      }
      return this;
    }

    public UpdateProductCommandBuilder images(List<String> images) {
      if (images != null) {
        this.images = ProductImages.create(images);
      }
      return this;
    }

    public UpdateProductCommandBuilder barcode(String barcode) {
      this.barcode = barcode;
      return this;
    }

    public UpdateProductCommandBuilder requiresPrescription(boolean requiresPrescription) {
      this.requiresPrescription = requiresPrescription;
      return this;
    }

    public UpdateProductCommandBuilder contraindications(List<String> contraindications) {
      if (contraindications != null) {
        this.contraindications = new ArrayList<>(contraindications);
      } else {
        this.contraindications = null;
      }
      return this;
    }

    public UpdateProductCommandBuilder dosage(String dosage) {
      if (dosage != null) {
        this.dosage = Dosage.create(dosage);
      }
      return this;
    }

    public UpdateProductCommandBuilder administration(String administration) {
      if (administration != null) {
        this.administration = Administration.create(administration);
      }
      return this;
    }

    public UpdateProductCommandBuilder expirationRange(Integer minMonths, Integer maxMonths) {
      if (minMonths != null || maxMonths != null) {
        this.expirationRange = ExpirationRange.create(minMonths != null ? minMonths : 0,
            maxMonths != null ? maxMonths : 0);
      }
      return this;
    }

    public UpdateProductCommand build() {
      return new UpdateProductCommand(productId, name, sku, description, activeIngredient, manufacturer, classification,
          price, barcode, requiresPrescription, contraindications, images, dosage, administration, expirationRange);
    }

    public UpdateProductCommand builderFromPrimitives() {
      return build();
    }
  }
}
