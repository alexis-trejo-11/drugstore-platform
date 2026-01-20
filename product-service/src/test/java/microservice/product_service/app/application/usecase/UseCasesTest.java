package microservice.product_service.app.application.usecase;

import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.application.port.out.ProductRepository;
import microservice.product_service.app.domain.model.CreateProductParams;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.enums.ProductStatus;
import microservice.product_service.app.domain.model.valueobjects.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.PageImpl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

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
        .code(ProductCode.create("ABC123"))
        .barcode("12345678")
        .name(ProductName.create("Sample"))
        .description(ProductDescription.create("desc"))
        .activeIngredient(ActiveIngredient.create("Ing"))
        .manufacturer(Manufacturer.createRequired("Maker"))
        .category(ProductCategory.ANALGESICS)
        .price(ProductPrice.create(BigDecimal.valueOf(9.99)))
        .dates(ProductDates.create(LocalDateTime.now().minusDays(5), LocalDateTime.now().plusDays(200)))
        .requiresPrescription(false)
        .status(ProductStatus.INACTIVE)
        .contraindications(java.util.Set.of())
        .dosage(Dosage.create("1 tablet"))
        .administration(Administration.ORAL)
        .build();

    return Product.create(params);
  }

  @Test
  void createProduct_callsRepositorySave() {
    CreateProductCommand cmd = new CreateProductCommand(
        "Sample",
        "ABC123",
        "desc",
        "Ing",
        "Maker",
        ProductCategory.ANALGESICS,
        BigDecimal.valueOf(9.99),
        0,
        "12345678",
        null,
        LocalDateTime.now().plusDays(100),
        LocalDateTime.now().minusDays(10),
        false,
        ProductStatus.INACTIVE,
        List.of(),
        "1 tablet",
        "ORAL");

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
  void updateProduct_appliesChanges() {
    Product existing = buildSampleProduct();
    when(repository.findByID(existing.getId())).thenReturn(Optional.of(existing));
    when(repository.existsByCode(any())).thenReturn(false);
    when(repository.existsByCodeBarcode(anyString())).thenReturn(false);
    when(repository.save(any(Product.class))).thenAnswer(i -> i.getArgument(0));

    UpdateProductCommand cmd = new UpdateProductCommand(
        existing.getId(),
        ProductName.create("NewName"),
        ProductDescription.create("NewDesc"),
        ActiveIngredient.create("NewIng"),
        Manufacturer.createRequired("NewMaker"),
        ProductCategory.ANALGESICS,
        ProductPrice.create(BigDecimal.valueOf(5)),
        ProductCode.create("XYZ999"),
        "87654321",
        false,
        java.util.Set.of(),
        Dosage.create("2 tablets"),
        Administration.ORAL);

    Product updated = updateUseCase.updateProduct(cmd);
    assertThat(updated.getName().value()).isEqualTo("NewName");
    assertThat(updated.getDescription().getValue()).isEqualTo("NewDesc");
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

    assertThrows(microservice.product_service.app.domain.exception.ProductNotFoundException.class,
        () -> deleteUseCase.deleteProduct(id));
  }

  @Test
  void searchProducts_returnsList() {
    Product p = buildSampleProduct();
    when(repository.search(any())).thenReturn(new PageImpl<>(List.of(p)));

    SearchProductsQuery q = new SearchProductsQuery("Sample", ProductCategory.ANALGESICS, null, null, null, 0, 10);
    List<Product> res = searchUseCase.searchProducts(q);

    assertThat(res).hasSize(1);
    assertThat(res.get(0)).isEqualTo(p);
  }

  @Test
  void restore_callsRepository() {
    ProductID id = ProductID.generate();
    doNothing().when(repository).restoreByID(id);

    restoreUseCase.restoreByID(id);

    verify(repository).restoreByID(id);
  }
}
