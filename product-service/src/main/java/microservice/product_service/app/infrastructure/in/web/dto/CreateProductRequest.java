package microservice.product_service.app.infrastructure.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.enums.ProductSubcategory;
import microservice.product_service.app.domain.model.enums.ProductType;

@Data
public class CreateProductRequest {

  @Size(max = 100, message = "SKU must not exceed 100 characters")
  private String sku;

  @NotBlank(message = "Product name is required")
  @Size(max = 255, message = "Product name must not exceed 255 characters")
  private String name;

  @Size(max = 1000, message = "Description must not exceed 1000 characters")
  private String description;

  @Size(max = 255, message = "Active ingredient must not exceed 255 characters")
  private String activeIngredient;

  @Size(max = 255, message = "Manufacturer must not exceed 255 characters")
  private String manufacturer;

  private ProductType type;

  @NotNull(message = "Category is required")
  private ProductCategory category;

  private ProductSubcategory subcategory;

  @NotNull(message = "Price is required")
  @DecimalMin(value = "0.01", message = "Price must be positive")
  private BigDecimal price;

  @Size(max = 50, message = "Barcode must not exceed 50 characters")
  private String barcode;

  private List<String> images;

  private Integer expirationMinMonths;

  private Integer expirationMaxMonths;

  private boolean requiresPrescription;

  private ProductStatus status;

  private List<String> contraindications;

  @Size(max = 255, message = "Dosage must not exceed 255 characters")
  private String dosage;

  @Size(max = 500, message = "Administration must not exceed 500 characters")
  private String administration;

  public CreateProductCommand toCommand() {
    return CreateProductCommand.builder()
        .sku(this.getSku())
        .name(this.getName())
        .description(this.getDescription())
        .activeIngredient(this.getActiveIngredient())
        .manufacturer(this.getManufacturer())
        .type(this.getType())
        .category(this.getCategory())
        .subcategory(this.getSubcategory())
        .price(this.getPrice())
        .barcode(this.getBarcode())
        .images(this.getImages())
        .expirationMinMonths(this.getExpirationMinMonths())
        .expirationMaxMonths(this.getExpirationMaxMonths())
        .requiresPrescription(this.isRequiresPrescription())
        .status(this.getStatus())
        .contraindications(this.getContraindications())
        .dosage(this.getDosage())
        .administration(this.getAdministration())
        .build();
  }

}
