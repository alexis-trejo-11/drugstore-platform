package microservice.product_service.app.application.usecase;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;

import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.in.query.GetProductByBarCodeQuery;
import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.query.GetProductBySKUQuery;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.application.port.out.ProductRepository;
import microservice.product_service.app.domain.exception.ProductNotFoundException;
import microservice.product_service.app.domain.model.CreateProductParams;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductStatus;
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

public class UseCasesTest {

  private ProductRepository repository;
  private CreateProductUseCase createUseCase;
  private GetProductUseCases getUseCase;
  private UpdateProductUseCase updateUseCase;
  private DeleteProductUseCase deleteUseCase;
  private SearchProductsUseCase searchUseCase;
  private RestoreProductUseCase restoreUseCase;

  @BeforeEach
  void setup() {
    repository = mock(ProductRepository.class);
    createUseCase = new CreateProductUseCase(repository);
    getUseCase = new GetProductUseCases(repository);
    updateUseCase = new UpdateProductUseCase(repository);
    deleteUseCase = new DeleteProductUseCase(repository);
    searchUseCase = new SearchProductsUseCase(repository);
    restoreUseCase = new RestoreProductUseCase(repository);
  }

  private Product buildSampleProduct() {
    CreateProductParams params = CreateProductParams.builder()
        .sku(SKU.create("TEST-SKU-001"))
        .barcode("12345678")
        .name(ProductName.create("Sample Product"))
        .description(ProductDescription.create("Sample description"))
        .activeIngredient(ActiveIngredient.create("Sample Ingredient"))
        .manufacturer(Manufacturer.createRequired("Sample Manufacturer"))
        .classification(ProductClassification.create(ProductType.MEDICATION, ProductCategory.ANALGESICS,
            ProductSubcategory.NOT_SPECIFIED))
        .price(ProductPrice.create(BigDecimal.valueOf(9.99)))
        .expirationRange(ExpirationRange.create(12, 36))
        .requiresPrescription(false)
        .status(ProductStatus.ACTIVE)
        .contraindications(List.of("Test contraindication"))
        .dosage(Dosage.create("1 tablet"))
        .administration(Administration.ORAL)
        .images(ProductImages.EMPTY)
        .build();

    return Product.create(params);
  }

  @Test
  void createProduct_callsRepositorySave() {
    CreateProductCommand cmd = CreateProductCommand.builder()
        .name("Sample Product")
        .sku("TEST-SKU-001")
        .description("Sample description")
        .activeIngredient("Sample Ingredient")
        .manufacturer("Sample Manufacturer")
        .type(ProductType.MEDICATION)
        .category(ProductCategory.ANALGESICS)
        .subcategory(ProductSubcategory.NOT_SPECIFIED)
        .price(BigDecimal.valueOf(9.99))
        .barcode("12345678")
        .expirationMinMonths(12)
        .expirationMaxMonths(36)
        .requiresPrescription(false)
        .status(ProductStatus.ACTIVE)
        .contraindications(List.of("Test contraindication"))
        .dosage("1 tablet")
        .administration("ORAL")
        .build();

    when(repository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    Product result = createUseCase.createProduct(cmd);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isNotNull();
    verify(repository, times(1)).save(any(Product.class));
  }

  @Test
  void getProduct_existing_returnsProduct() {
    Product p = buildSampleProduct();
    when(repository.findByID(p.getId())).thenReturn(Optional.of(p));

    Product out = getUseCase.getProduct(new GetProductByIDQuery(p.getId()));

    assertThat(out).isEqualTo(p);
  }

  @Test
  void getProductBySKU_existing_returnsProduct() {
    Product p = buildSampleProduct();
    var sku = p.getSku();
    when(repository.findBySKU(sku)).thenReturn(Optional.of(p));

    Product out = getUseCase.getProduct(new GetProductBySKUQuery(sku));

    assertThat(out).isEqualTo(p);
    verify(repository).findBySKU(sku);
  }

  @Test
  void getProductByBarcode_existing_returnsProduct() {
    Product p = buildSampleProduct();
    String barcode = p.getBarcode();
    when(repository.findByBarCode(barcode)).thenReturn(Optional.of(p));

    Product out = getUseCase.getProduct(new GetProductByBarCodeQuery(barcode));

    assertThat(out).isEqualTo(p);
    verify(repository).findByBarCode(barcode);
  }

  @Test
  void getProductBySKU_missing_throws() {
    SKU sku = new SKU("MISSING-SKU-001");
    when(repository.findBySKU(sku)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class,
        () -> getUseCase.getProduct(new GetProductBySKUQuery(sku)));
  }

  @Test
  void getProductByBarcode_missing_throws() {
    String barcode = "999999999";
    when(repository.findByBarCode(barcode)).thenReturn(Optional.empty());

    assertThrows(ProductNotFoundException.class,
        () -> getUseCase.getProduct(new GetProductByBarCodeQuery(barcode)));
  }

  @Test
  void updateProduct_appliesChanges() {
    Product existing = buildSampleProduct();
    when(repository.findByID(existing.getId())).thenReturn(Optional.of(existing));
    when(repository.existsByBarCode(anyString())).thenReturn(false);
    when(repository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    UpdateProductCommand cmd = UpdateProductCommand.builder()
        .productId(existing.getId().toString())
        .name("Updated Name")
        .description("Updated Description")
        .activeIngredient("Updated Ingredient")
        .manufacturer("Updated Manufacturer")
        .classification(ProductType.MEDICATION, ProductCategory.ANALGESICS, ProductSubcategory.NOT_SPECIFIED)
        .price(BigDecimal.valueOf(15.99))
        .barcode("87654321")
        .requiresPrescription(false)
        .contraindications(List.of("Updated contraindication"))
        .dosage("2 tablets")
        .administration("ORAL")
        .build();

    Product updated = updateUseCase.updateProduct(cmd);
    assertThat(updated.getName().value()).isEqualTo("Updated Name");
    assertThat(updated.getDescription().getValue()).isEqualTo("Updated Description");
    verify(repository).save(any(Product.class));
  }

  @Test
  void deleteProduct_existing_callsDelete() {
    Product p = buildSampleProduct();
    when(repository.existsByID(p.getId())).thenReturn(true);

    deleteUseCase.deleteProduct(p.getId());

    verify(repository, times(1)).deleteByID(p.getId());
  }

  @Test
  void deleteProduct_missing_throws() {
    ProductID id = ProductID.generate();
    when(repository.existsByID(id)).thenReturn(false);

    assertThrows(ProductNotFoundException.class,
        () -> deleteUseCase.deleteProduct(id));
  }

  @Test
  void searchProducts_returnsList() {
    Product p = buildSampleProduct();
    Page<Product> productPage = new PageImpl<>(List.of(p));
    when(repository.search(any())).thenReturn(productPage);

    SearchProductsQuery q = new SearchProductsQuery("Sample", ProductCategory.ANALGESICS, null, null, null, 0, 10);
    Page<Product> resultPage = searchUseCase.searchProducts(q);

    assertThat(resultPage).isNotNull();
    assertThat(resultPage.getContent()).hasSize(1);
    assertThat(resultPage.getContent().get(0)).isEqualTo(p);
  }

  @Test
  void restore_callsRepository() {
    ProductID id = ProductID.generate();
    Product deletedProduct = buildSampleProduct();
    when(repository.findDeletedByID(id)).thenReturn(Optional.of(deletedProduct));
    when(repository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    restoreUseCase.restoreByID(id);

    verify(repository).findDeletedByID(id);
    verify(repository).save(any(Product.class));
  }
}
