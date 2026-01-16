package microservice.product_service.app.domain.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.Test;

import microservice.product_service.app.domain.exception.ExpiredProductException;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.valueobjects.ActiveIngredient;
import microservice.product_service.app.domain.model.valueobjects.Administration;
import microservice.product_service.app.domain.model.valueobjects.Dosage;
import microservice.product_service.app.domain.model.valueobjects.Manufacturer;
import microservice.product_service.app.domain.model.valueobjects.ProductCode;
import microservice.product_service.app.domain.model.valueobjects.ProductDates;
import microservice.product_service.app.domain.model.valueobjects.ProductDescription;
import microservice.product_service.app.domain.model.valueobjects.ProductName;
import microservice.product_service.app.domain.model.valueobjects.ProductPrice;

public class ProductTest {

  @Test
  void create_and_basic_behaviour() {
    CreateProductParams params = CreateProductParams.builder()
        .code(ProductCode.NONE)
        .barcode("12345678")
        .name(new ProductName("My Product"))
        .description(new ProductDescription("Desc"))
        .activeIngredient(new ActiveIngredient("Ing"))
        .manufacturer(new Manufacturer("Maker"))
        .category(ProductCategory.ANALGESICS)
        .price(ProductPrice.create(BigDecimal.valueOf(9.99)))
        .dates(ProductDates.create(LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(10)))
        .requiresPrescription(false)
        .status(ProductStatus.INACTIVE)
        .contraindications(Set.of())
        .dosage(Dosage.create("1 tablet"))
        .administration(Administration.ORAL)
        .build();

    Product product = Product.create(params);
    assertThat(product).isNotNull();
    assertThat(product.isExpired()).isFalse();

    product.activate();
    assertThat(product.getStatus()).isEqualTo(ProductStatus.ACTIVE);

    product.deactivate();
    assertThat(product.getStatus()).isEqualTo(ProductStatus.INACTIVE);

    product.softDelete();
    assertThat(product.isDeleted()).isTrue();

    product.restore();
    assertThat(product.isDeleted()).isFalse();
  }

  @Test
  void activate_expired_throws() {
    CreateProductParams params = CreateProductParams.builder()
        .code(ProductCode.NONE)
        .barcode("12345678")
        .name(new ProductName("Expired"))
        .description(new ProductDescription("Old"))
        .activeIngredient(new ActiveIngredient("Ing"))
        .manufacturer(new Manufacturer("Maker"))
        .category(ProductCategory.ANALGESICS)
        .price(ProductPrice.create(BigDecimal.valueOf(1)))
        .dates(ProductDates.create(LocalDateTime.now().minusDays(10), LocalDateTime.now().minusDays(1)))
        .requiresPrescription(false)
        .status(ProductStatus.INACTIVE)
        .contraindications(Set.of())
        .dosage(Dosage.create("1 tablet"))
        .administration(Administration.ORAL)
        .build();

    Product product = Product.create(params);
    assertThat(product.isExpired()).isTrue();
    assertThrows(ExpiredProductException.class, product::activate);
  }
}
