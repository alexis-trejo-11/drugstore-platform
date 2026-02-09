package microservice.product_service.app.adapter.out.persistence;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;

import microservice.product_service.app.core.domain.model.CreateProductParams;
import microservice.product_service.app.core.domain.model.Product;
import microservice.product_service.app.core.domain.model.enums.ProductCategory;
import microservice.product_service.app.core.domain.model.enums.ProductStatus;
import microservice.product_service.app.core.domain.model.enums.ProductSubcategory;
import microservice.product_service.app.core.domain.model.enums.ProductType;
import microservice.product_service.app.core.domain.model.valueobjects.ActiveIngredient;
import microservice.product_service.app.core.domain.model.valueobjects.Administration;
import microservice.product_service.app.core.domain.model.valueobjects.Dosage;
import microservice.product_service.app.core.domain.model.valueobjects.ExpirationRange;
import microservice.product_service.app.core.domain.model.valueobjects.Manufacturer;
import microservice.product_service.app.core.domain.model.valueobjects.ProductClassification;
import microservice.product_service.app.core.domain.model.valueobjects.ProductDescription;
import microservice.product_service.app.core.domain.model.valueobjects.ProductID;
import microservice.product_service.app.core.domain.model.valueobjects.ProductImages;
import microservice.product_service.app.core.domain.model.valueobjects.ProductName;
import microservice.product_service.app.core.domain.model.valueobjects.ProductPrice;
import microservice.product_service.app.core.domain.model.valueobjects.SKU;
import microservice.product_service.app.core.domain.specification.ProductSearchCriteria;
import microservice.product_service.app.adapter.out.persistence.mapper.ProductModelMapper;
import microservice.product_service.app.adapter.out.persistence.specification.ProductSpecificationBuilder;

@ExtendWith(MockitoExtension.class)
public class ProductRepositoryImplTest {

  @Mock
  private ProductJpaRepository jpaRepository;

  @Mock
  private ProductModelMapper mapper;

  @Mock
  private ProductSpecificationBuilder specificationBuilder;

  @InjectMocks
  private ProductRepositoryImpl productRepository;

  private Product sampleProduct;
  private ProductModel sampleProductModel;
  private ProductID productId;
  private SKU sku;

  @BeforeEach
  void setUp() {
    productId = ProductID.generate();
    sku = SKU.create("TEST-SKU-001");

    // Create sample domain product
    CreateProductParams params = CreateProductParams.builder()
        .sku(sku)
        .name(ProductName.create("Test Product"))
        .description(ProductDescription.create("Test Description"))
        .activeIngredient(ActiveIngredient.create("Test Ingredient"))
        .manufacturer(Manufacturer.createRequired("Test Manufacturer"))
        .classification(ProductClassification.create(ProductType.MEDICATION, ProductCategory.ANALGESICS,
            ProductSubcategory.NOT_SPECIFIED))
        .price(ProductPrice.create(BigDecimal.valueOf(10.99)))
        .expirationRange(ExpirationRange.create(12, 36))
        .requiresPrescription(false)
        .status(ProductStatus.ACTIVE)
        .contraindications(List.of("Test contraindication"))
        .dosage(Dosage.create("1 tablet"))
        .administration(Administration.ORAL)
        .images(ProductImages.EMPTY)
        .barcode("1234567890")
        .build();

    sampleProduct = Product.create(params);

    // Create sample entity model
    sampleProductModel = new ProductModel();
    sampleProductModel.setId(productId.value());
    sampleProductModel.setSKU(sku.value());
    sampleProductModel.setName("Test Product");
    sampleProductModel.setDescription("Test Description");
    sampleProductModel.setActiveIngredient("Test Ingredient");
    sampleProductModel.setManufacturer("Test Manufacturer");
    sampleProductModel.setType(ProductType.MEDICATION);
    sampleProductModel.setCategory(ProductCategory.ANALGESICS);
    sampleProductModel.setSubcategory(ProductSubcategory.NOT_SPECIFIED);
    sampleProductModel.setPrice(BigDecimal.valueOf(10.99));
    sampleProductModel.setBarcode("1234567890");
    sampleProductModel.setRequiresPrescription(false);
    sampleProductModel.setStatus(ProductStatus.ACTIVE);
    sampleProductModel.setContraindications(List.of("Test contraindication"));
    sampleProductModel.setDosage("1 tablet");
    sampleProductModel.setAdministration("ORAL");
    sampleProductModel.setCreatedAt(LocalDateTime.now());
    sampleProductModel.setUpdatedAt(LocalDateTime.now());
  }

  @Test
  void save_shouldSaveProductAndReturnMappedDomain() {
    // Given
    when(mapper.fromDomain(sampleProduct)).thenReturn(sampleProductModel);
    when(jpaRepository.save(sampleProductModel)).thenReturn(sampleProductModel);
    when(mapper.toDomain(sampleProductModel)).thenReturn(sampleProduct);

    // When
    Product result = productRepository.save(sampleProduct);

    // Then
    assertThat(result).isEqualTo(sampleProduct);
    verify(mapper).fromDomain(sampleProduct);
    verify(jpaRepository).save(sampleProductModel);
    verify(mapper).toDomain(sampleProductModel);
  }

  @Test
  void findByID_whenProductExists_shouldReturnProduct() {
    // Given
    when(jpaRepository.findByIdAndDeletedAtIsNull(productId.value())).thenReturn(Optional.of(sampleProductModel));
    when(mapper.toDomain(sampleProductModel)).thenReturn(sampleProduct);

    // When
    Optional<Product> result = productRepository.findByID(productId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(sampleProduct);
    verify(jpaRepository).findByIdAndDeletedAtIsNull(productId.value());
    verify(mapper).toDomain(sampleProductModel);
  }

  @Test
  void findByID_whenProductNotExists_shouldReturnEmpty() {
    // Given
    when(jpaRepository.findByIdAndDeletedAtIsNull(productId.value())).thenReturn(Optional.empty());

    // When
    Optional<Product> result = productRepository.findByID(productId);

    // Then
    assertThat(result).isEmpty();
    verify(jpaRepository).findByIdAndDeletedAtIsNull(productId.value());
    verify(mapper, never()).toDomain(any());
  }

  @Test
  void findBySKU_whenProductExists_shouldReturnProduct() {
    // Given
    when(jpaRepository.findOne(any(Specification.class))).thenReturn(Optional.of(sampleProductModel));
    when(mapper.toDomain(sampleProductModel)).thenReturn(sampleProduct);

    // When
    Optional<Product> result = productRepository.findBySKU(sku);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(sampleProduct);
    verify(jpaRepository).findOne(any(Specification.class));
    verify(mapper).toDomain(sampleProductModel);
  }

  @Test
  void findBySKU_whenProductNotExists_shouldReturnEmpty() {
    // Given
    when(jpaRepository.findOne(any(Specification.class))).thenReturn(Optional.empty());

    // When
    Optional<Product> result = productRepository.findBySKU(sku);

    // Then
    assertThat(result).isEmpty();
    verify(jpaRepository).findOne(any(Specification.class));
    verify(mapper, never()).toDomain(any());
  }

  @Test
  void findByBarCode_whenProductExists_shouldReturnProduct() {
    // Given
    String barcode = "1234567890";
    when(jpaRepository.findByBarcodeAndDeletedAtIsNull(barcode)).thenReturn(Optional.of(sampleProductModel));
    when(mapper.toDomain(sampleProductModel)).thenReturn(sampleProduct);

    // When
    Optional<Product> result = productRepository.findByBarCode(barcode);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(sampleProduct);
    verify(jpaRepository).findByBarcodeAndDeletedAtIsNull(barcode);
    verify(mapper).toDomain(sampleProductModel);
  }

  @Test
  void findByBarCode_whenProductNotExists_shouldReturnEmpty() {
    // Given
    String barcode = "1234567890";
    when(jpaRepository.findByBarcodeAndDeletedAtIsNull(barcode)).thenReturn(Optional.empty());

    // When
    Optional<Product> result = productRepository.findByBarCode(barcode);

    // Then
    assertThat(result).isEmpty();
    verify(jpaRepository).findByBarcodeAndDeletedAtIsNull(barcode);
    verify(mapper, never()).toDomain(any());
  }

  @Test
  void search_shouldReturnPagedResults() {
    // Given
    ProductSearchCriteria criteria = ProductSearchCriteria.builder()
        .name("Test")
        .category(ProductCategory.ANALGESICS)
        .page(0)
        .size(10)
        .build();

    Specification<ProductModel> spec = mock(Specification.class);
    Page<ProductModel> entityPage = new PageImpl<>(List.of(sampleProductModel));
    Page<Product> domainPage = new PageImpl<>(List.of(sampleProduct));

    when(specificationBuilder.build(criteria)).thenReturn(spec);
    when(jpaRepository.findAll(eq(spec), any(PageRequest.class))).thenReturn(entityPage);
    when(mapper.toDomainPage(entityPage)).thenReturn(domainPage);

    // When
    Page<Product> result = productRepository.search(criteria);

    // Then
    assertThat(result).isEqualTo(domainPage);
    assertThat(result.getContent()).hasSize(1);
    verify(specificationBuilder).build(criteria);
    verify(jpaRepository).findAll(eq(spec), any(PageRequest.class));
    verify(mapper).toDomainPage(entityPage);
  }

  @Test
  void count_shouldReturnCount() {
    // Given
    ProductSearchCriteria criteria = ProductSearchCriteria.builder()
        .name("Test")
        .build();

    Specification<ProductModel> spec = mock(Specification.class);
    when(specificationBuilder.build(criteria)).thenReturn(spec);
    when(jpaRepository.count(spec)).thenReturn(5L);

    // When
    long result = productRepository.count(criteria);

    // Then
    assertThat(result).isEqualTo(5L);
    verify(specificationBuilder).build(criteria);
    verify(jpaRepository).count(spec);
  }

  @Test
  void existsBySKU_whenExists_shouldReturnTrue() {
    // Given
    when(jpaRepository.existsBySKU(sku.value())).thenReturn(true);

    // When
    boolean result = productRepository.existsBySKU(sku);

    // Then
    assertThat(result).isTrue();
    verify(jpaRepository).existsBySKU(sku.value());
  }

  @Test
  void existsBySKU_whenNotExists_shouldReturnFalse() {
    // Given
    when(jpaRepository.existsBySKU(sku.value())).thenReturn(false);

    // When
    boolean result = productRepository.existsBySKU(sku);

    // Then
    assertThat(result).isFalse();
    verify(jpaRepository).existsBySKU(sku.value());
  }

  @Test
  void existsByID_whenExists_shouldReturnTrue() {
    // Given
    when(jpaRepository.existsById(productId.value())).thenReturn(true);

    // When
    boolean result = productRepository.existsByID(productId);

    // Then
    assertThat(result).isTrue();
    verify(jpaRepository).existsById(productId.value());
  }

  @Test
  void existsByID_whenNotExists_shouldReturnFalse() {
    // Given
    when(jpaRepository.existsById(productId.value())).thenReturn(false);

    // When
    boolean result = productRepository.existsByID(productId);

    // Then
    assertThat(result).isFalse();
    verify(jpaRepository).existsById(productId.value());
  }

  @Test
  void existsByBarCode_whenExists_shouldReturnTrue() {
    // Given
    String barcode = "1234567890";
    when(jpaRepository.existsByBarcode(barcode)).thenReturn(true);

    // When
    boolean result = productRepository.existsByBarCode(barcode);

    // Then
    assertThat(result).isTrue();
    verify(jpaRepository).existsByBarcode(barcode);
  }

  @Test
  void existsByBarCode_whenNotExists_shouldReturnFalse() {
    // Given
    String barcode = "1234567890";
    when(jpaRepository.existsByBarcode(barcode)).thenReturn(false);

    // When
    boolean result = productRepository.existsByBarCode(barcode);

    // Then
    assertThat(result).isFalse();
    verify(jpaRepository).existsByBarcode(barcode);
  }

  @Test
  void deleteByID_shouldCallJpaRepositoryDelete() {
    // When
    productRepository.deleteByID(productId);

    // Then
    verify(jpaRepository).deleteById(productId.value());
  }

  @Test
  void findDeletedByID_whenDeletedProductExists_shouldReturnProduct() {
    // Given
    when(jpaRepository.findByIdAndDeletedAtNotNull(productId.value())).thenReturn(Optional.of(sampleProductModel));
    when(mapper.toDomain(sampleProductModel)).thenReturn(sampleProduct);

    // When
    Optional<Product> result = productRepository.findDeletedByID(productId);

    // Then
    assertThat(result).isPresent();
    assertThat(result.get()).isEqualTo(sampleProduct);
    verify(jpaRepository).findByIdAndDeletedAtNotNull(productId.value());
    verify(mapper).toDomain(sampleProductModel);
  }

  @Test
  void findDeletedByID_whenDeletedProductNotExists_shouldReturnEmpty() {
    // Given
    when(jpaRepository.findByIdAndDeletedAtNotNull(productId.value())).thenReturn(Optional.empty());

    // When
    Optional<Product> result = productRepository.findDeletedByID(productId);

    // Then
    assertThat(result).isEmpty();
    verify(jpaRepository).findByIdAndDeletedAtNotNull(productId.value());
    verify(mapper, never()).toDomain(any());
  }
}
