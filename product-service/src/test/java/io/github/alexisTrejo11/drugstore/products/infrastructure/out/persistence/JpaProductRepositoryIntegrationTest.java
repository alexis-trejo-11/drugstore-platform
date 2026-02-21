package io.github.alexisTrejo11.drugstore.products.infrastructure.out.persistence;

import io.github.alexisTrejo11.drugstore.products.core.domain.model.CreateProductParams;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.Product;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductID;
import io.github.alexisTrejo11.drugstore.products.app.domain.model.valueobjects.ProductCode;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductName;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductDescription;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ActiveIngredient;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Manufacturer;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.enums.ProductCategory;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductPrice;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.ProductDates;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Dosage;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.valueobjects.Administration;
import io.github.alexisTrejo11.drugstore.products.core.port.output.ProductRepository;
import io.github.alexisTrejo11.drugstore.products.core.domain.specification.ProductSearchCriteria;
import io.github.alexisTrejo11.drugstore.products.core.domain.specification.SortCriteria;
import io.github.alexisTrejo11.drugstore.products.core.domain.model.enums.ProductStatus;
import io.github.alexisTrejo11.drugstore.products.adapter.out.persistence.ProductJpaRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;
import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import org.springframework.data.domain.Page;

// TODO: Update Tests to new implementation
@SpringBootTest(properties = {
    "spring.cloud.config.enabled=false",
    "spring.cloud.config.fail-fast=false",
    "spring.main.allow-bean-definition-overriding=true"
})
public class JpaProductRepositoryIntegrationTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private ProductJpaRepository jpaProductRepository;

  @Test
  @Transactional
  void softDeleteAndRestoreFlow() {
    // Build domain product
    CreateProductParams params = CreateProductParams.builder()
        .code(ProductCode.NONE)
        .barcode("12345678")
        .name(new ProductName("My Product"))
        .description(new ProductDescription("Desc"))
        .activeIngredient(new ActiveIngredient("Ing"))
        .manufacturer(new Manufacturer("Maker"))
        .category(ProductCategory.ANALGESICS)
        .price(new ProductPrice(
            BigDecimal.valueOf(9.99)))
        .dates(new ProductDates(
            LocalDateTime.now().minusDays(10), LocalDateTime.now().plusDays(100)))
        .requiresPrescription(false)
        .status(ProductStatus.INACTIVE)
        .contraindications(Set.of())
        .dosage(new Dosage("1 tablet"))
        .administration(new Administration("ORAL"))
        .build();

    Product product = Product.create(params);

    // Save
    Product saved = productRepository.save(product);
    assertThat(saved).isNotNull();
    ProductID id = saved.getId();

    // Exists and find
    Optional<Product> found = productRepository.findByID(id);
    assertThat(found).isPresent();

    // Soft delete
    productRepository.deleteByID(id);

    // After deletion, findByID should return empty
    Optional<Product> afterDelete = productRepository.findByID(id);
    assertThat(afterDelete).isEmpty();

    // After deletion, existence checks should be false
    assertThat(productRepository.existsByID(id)).isFalse();
    assertThat(productRepository.existsByCode(saved.getCode())).isFalse();

    // Restore
    productRepository.restoreByID(id);

    // After restore, findByID should return present
    Optional<Product> afterRestore = productRepository.findByID(id);
    assertThat(afterRestore).isPresent();

    // After restore, existence checks should be true again
    assertThat(productRepository.existsByID(id)).isTrue();
  }

  @Test
  @Transactional
  void repositoryCrudAndSearchFlow() {
    // Create and save multiple products
    Product a = Product.create(CreateProductParams.builder()
        .code(ProductCode.NONE)
        .barcode("12345678")
        .name(new ProductName("A Product"))
        .description(new ProductDescription("A"))
        .activeIngredient(new ActiveIngredient("IA"))
        .manufacturer(new Manufacturer("M1"))
        .category(ProductCategory.ANALGESICS)
        .price(new ProductPrice(
            BigDecimal.valueOf(1)))
        .dates(new ProductDates(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(200)))
        .requiresPrescription(false)
        .status(ProductStatus.INACTIVE)
        .contraindications(Set.of())
        .dosage(new Dosage("dosage"))
        .administration(new Administration("ORAL"))
        .build());

    Product b = Product.create(CreateProductParams.builder()
        .code(ProductCode.NONE)
        .barcode("12345679")
        .name(new ProductName("B Product"))
        .description(new ProductDescription("B"))
        .activeIngredient(new ActiveIngredient("IB"))
        .manufacturer(new Manufacturer("M2"))
        .category(ProductCategory.ANALGESICS)
        .price(new ProductPrice(
            BigDecimal.valueOf(2)))
        .dates(new ProductDates(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(200)))
        .requiresPrescription(false)
        .status(ProductStatus.INACTIVE)
        .contraindications(Set.of())
        .dosage(new Dosage("dosage"))
        .administration(new Administration("ORAL"))
        .build());

    Product c = Product.create(CreateProductParams.builder()
        .code(ProductCode.NONE)
        .barcode("12345670")
        .name(new ProductName("C Product"))
        .description(new ProductDescription("C"))
        .activeIngredient(new ActiveIngredient("IC"))
        .manufacturer(new Manufacturer("M3"))
        .category(ProductCategory.ANALGESICS)
        .price(new ProductPrice(
            BigDecimal.valueOf(3)))
        .dates(new ProductDates(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(200)))
        .requiresPrescription(false)
        .status(ProductStatus.INACTIVE)
        .contraindications(Set.of())
        .dosage(new Dosage("dosage"))
        .administration(new Administration("ORAL"))
        .build());

    Product savedA = productRepository.save(a);
    Product savedB = productRepository.save(b);
    Product savedC = productRepository.save(c);

    // Existence checks
    assertThat(productRepository.existsByID(savedA.getId())).isTrue();
    assertThat(productRepository.existsByID(savedB.getId())).isTrue();
    assertThat(productRepository.existsByID(savedC.getId())).isTrue();

    // Find by id
    assertThat(productRepository.findByID(savedB.getId())).isPresent();

    // Search (paged) - default criteria returns page 0 size 10 sorted unsorted
    ProductSearchCriteria criteria = ProductSearchCriteria.builder()
        .onlyActive(false) // Include inactive products
        .build();
    Page<Product> page = productRepository.search(criteria);
    assertThat(page.getTotalElements()).isEqualTo(3);
    assertThat(page.getContent()).hasSize(3);

    // Also test pagination: page size 2
    ProductSearchCriteria pagedCriteria = ProductSearchCriteria.builder()
        .onlyActive(false) // Include inactive products
        .page(0).size(2)
        .sortBy(SortCriteria.ascending("name")).build();
    Page<Product> paged = productRepository.search(pagedCriteria);
    assertThat(paged.getTotalElements()).isEqualTo(3);
    assertThat(paged.getSize()).isEqualTo(2);
    assertThat(paged.getNumberOfElements()).isEqualTo(2);

    // Count
    long count = productRepository.count(ProductSearchCriteria.builder()
        .onlyActive(false) // Include inactive products
        .build());
    assertThat(count).isEqualTo(3);
  }

  @Test
  @Transactional
  void softDeleteExposureAndRestoreViaJpaRepository() {
    // Save a product
    Product product = Product.create(CreateProductParams.builder()
        .code(ProductCode.NONE)
        .barcode("1234567890")
        .name(new ProductName("Jpa Product"))
        .description(new ProductDescription("Jpa"))
        .activeIngredient(new ActiveIngredient("I"))
        .manufacturer(new Manufacturer("M"))
        .category(ProductCategory.ANALGESICS)
        .price(new ProductPrice(
            BigDecimal.valueOf(5)))
        .dates(new ProductDates(
            LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(200)))
        .requiresPrescription(false)
        .status(ProductStatus.INACTIVE)
        .contraindications(Set.of())
        .dosage(new Dosage("dosage"))
        .administration(new Administration("ORAL"))
        .build());

    Product saved = productRepository.save(product);
    ProductID id = saved.getId();

    // Ensure visible via JPA default (non-deleted)
    assertThat(jpaProductRepository.findById(id.value().toString())).isPresent();

    // Soft delete using domain repository
    productRepository.deleteByID(id);

    // After soft-delete, JPA findById (non-include) should be empty
    assertThat(jpaProductRepository.findById(id.value().toString())).isNotPresent();

    // But include-deleted queries should return the entity
    assertThat(jpaProductRepository.findByIdIncludeDeleted(id.value().toString())).isPresent();

    // Restore using domain repository
    productRepository.restoreByID(id);

    // After restore, non-include JPA find should return the entity again
    assertThat(jpaProductRepository.findById(id.value().toString())).isPresent();
  }
}
