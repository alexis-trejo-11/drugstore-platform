package microservice.product_service.app.infrastructure.in.web.dto;

import java.math.BigDecimal;
import java.util.List;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductSubcategory;
import microservice.product_service.app.domain.model.enums.ProductType;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateProductRequest {
  @Size(max = 255, message = "Product name must not exceed 255 characters")
  private String name;

  @Size(max = 2000, message = "Description must not exceed 2000 characters")
  private String description;

  @Size(max = 255, message = "Active ingredient must not exceed 255 characters")
  private String activeIngredient;

  @Size(max = 255, message = "Manufacturer must not exceed 255 characters")
  private String manufacturer;

  private ProductType type;
  private ProductCategory category;
  private ProductSubcategory subcategory;

  @DecimalMin(value = "0.01", message = "Price must be positive")
  private BigDecimal price;

  @Size(max = 50, message = "Barcode must not exceed 50 characters")
  private String barcode;

  private Boolean requiresPrescription; // Use Boolean wrapper for nullability

  private List<String> contraindications; // Can be null, or empty list

  @Size(max = 255, message = "Dosage must not exceed 255 characters")
  private String dosage;

  @Size(max = 50, message = "Administration must not exceed 50 characters")
  private String administration;

  private List<String> images;

  private Integer expirationMinMonths;
  private Integer expirationMaxMonths;

  public UpdateProductCommand toCommand(String productId) {
    return UpdateProductCommand.builder()
        .productId(productId)
        .name(this.getName())
        .description(this.getDescription())
        .activeIngredient(this.getActiveIngredient())
        .manufacturer(this.getManufacturer())
        .classification(this.getType(), this.getCategory(), this.getSubcategory())
        .price(this.getPrice())
        .barcode(this.getBarcode())
        .requiresPrescription(Boolean.TRUE.equals(this.getRequiresPrescription()))
        .contraindications(this.getContraindications())
        .dosage(this.getDosage())
        .administration(this.getAdministration())
        .images(this.getImages())
        .expirationRange(this.getExpirationMinMonths(), this.getExpirationMaxMonths())
        .build();
  }

}
