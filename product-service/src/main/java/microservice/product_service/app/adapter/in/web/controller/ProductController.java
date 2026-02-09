package microservice.product_service.app.adapter.in.web.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import microservice.product_service.app.core.port.input.command.CreateProductCommand;
import microservice.product_service.app.core.port.input.command.UpdateProductCommand;
import microservice.product_service.app.core.port.input.query.GetProductByBarCodeQuery;
import microservice.product_service.app.core.port.input.query.GetProductByIDQuery;
import microservice.product_service.app.core.port.input.query.GetProductBySKUQuery;
import microservice.product_service.app.core.port.input.query.SearchProductsQuery;
import microservice.product_service.app.core.port.input.usecase.ProductCommandUseCases;
import microservice.product_service.app.core.port.input.usecase.ProductQueryUseCases;
import microservice.product_service.app.core.domain.model.Product;
import microservice.product_service.app.core.domain.model.enums.ProductCategory;
import microservice.product_service.app.core.domain.model.valueobjects.ProductID;
import microservice.product_service.app.adapter.in.web.annotations.CreateProductOperation;
import microservice.product_service.app.adapter.in.web.annotations.DeleteProductOperation;
import microservice.product_service.app.adapter.in.web.annotations.GetProductByBarcodeOperation;
import microservice.product_service.app.adapter.in.web.annotations.GetProductByIdOperation;
import microservice.product_service.app.adapter.in.web.annotations.GetProductBySKUOperation;
import microservice.product_service.app.adapter.in.web.annotations.ProductApiOperation;
import microservice.product_service.app.adapter.in.web.annotations.ProductApiParameters;
import microservice.product_service.app.adapter.in.web.annotations.ProductControllerTag;
import microservice.product_service.app.adapter.in.web.annotations.RestoreProductOperation;
import microservice.product_service.app.adapter.in.web.annotations.SearchProductsOperation;
import microservice.product_service.app.adapter.in.web.annotations.UpdateProductOperation;
import microservice.product_service.app.adapter.in.web.dto.CreateProductRequest;
import microservice.product_service.app.adapter.in.web.dto.ProductResponse;
import microservice.product_service.app.adapter.in.web.dto.UpdateProductRequest;
import microservice.product_service.app.adapter.in.web.mapper.ProductResponseMapper;

@RestController
@RequestMapping("/api/v2/products")
@ProductControllerTag
public class ProductController {

  private final ProductCommandUseCases commandUseCases;
  private final ProductQueryUseCases queryUseCases;
  private final ProductResponseMapper responseMapper;

  public ProductController(
      ProductCommandUseCases commandUseCases,
      ProductQueryUseCases queryUseCases,
      ProductResponseMapper responseMapper) {
    this.commandUseCases = commandUseCases;
    this.queryUseCases = queryUseCases;
    this.responseMapper = responseMapper;
  }

  @SearchProductsOperation
  @GetMapping
  public ResponseWrapper<PageResponse<ProductResponse>> searchProducts(
      @ProductApiParameters.SearchQuery @ModelAttribute @NotNull SearchProductsQuery query) {

    Page<Product> productPage = queryUseCases.searchProducts(query);
    PageResponse<ProductResponse> responsePage = responseMapper.toPageResponse(productPage);
    return ResponseWrapper.found(responsePage, "Product");
  }

  @ProductApiOperation(summary = "Get All Product Categories", description = "Retrieves a list of all valid product categories")
  @GetMapping("/categories")
  public ResponseWrapper<List<String>> getAllProductStatus() {
    List<String> categories = ProductCategory.getAllDisplayNames();
    return ResponseWrapper.found(categories, "Product Categories");
  }

  @GetProductByIdOperation
  @GetMapping("/{productId}")
  @RateLimit(profile = RateLimitProfile.PUBLIC)
  public ResponseWrapper<ProductResponse> getProductByID(
      @ProductApiParameters.ProductId @PathVariable @Valid @NotBlank String productId) {

    var query = GetProductByIDQuery.of(productId);
    Product product = queryUseCases.getProductByID(query);
    ProductResponse productResponse = responseMapper.toResponse(product);

    return ResponseWrapper.found(productResponse, "Product");
  }

  @GetProductBySKUOperation
  @GetMapping("/sku/{sku}")
  @RateLimit(profile = RateLimitProfile.PUBLIC)
  public ResponseWrapper<ProductResponse> getProductBySKU(
      @PathVariable @Valid @NotBlank String sku) {
    var query = GetProductBySKUQuery.from(sku);
    Product product = queryUseCases.getProductBySKU(query);
    ProductResponse productResponse = responseMapper.toResponse(product);

    return ResponseWrapper.found(productResponse, "Product");
  }

  @GetProductByBarcodeOperation
  @GetMapping("/barcode/{barcode}")
  @RateLimit(profile = RateLimitProfile.PUBLIC)
  public ResponseWrapper<ProductResponse> getProductByBarcode(
      @PathVariable @Valid @NotBlank String barcode) {
    var query = new GetProductByBarCodeQuery(barcode);
    Product product = queryUseCases.getProductByBarcode(query);
    ProductResponse productResponse = responseMapper.toResponse(product);
    return ResponseWrapper.found(productResponse, "Product");
  }

  @CreateProductOperation
  @PostMapping
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseEntity<ResponseWrapper<ProductID>> createProduct(
      @Valid @RequestBody @NotNull CreateProductRequest productRequest) {

    CreateProductCommand command = productRequest.toCommand();
    Product product = commandUseCases.createProduct(command);
    var response = ResponseWrapper.created(product.getId(), "Product");

    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  @UpdateProductOperation
  @PutMapping("/{productId}")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseEntity<ResponseWrapper<Void>> updateProduct(
      @ProductApiParameters.ProductId @PathVariable @Valid @NotBlank String productId,
      @RequestBody @Valid UpdateProductRequest request) {

    UpdateProductCommand command = request.toCommand(productId);
    commandUseCases.updateProduct(command);

    return ResponseEntity.ok(ResponseWrapper.updated(null, "Product"));
  }

  @RestoreProductOperation
  @PatchMapping("/{productId}/restore")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseEntity<ResponseWrapper<Void>> restoreProduct(
      @ProductApiParameters.ProductId @PathVariable @Valid @NotBlank String productId) {

    var productIdObj = ProductID.from(productId);
    commandUseCases.restoreProduct(productIdObj);

    return ResponseEntity.ok(ResponseWrapper.success(null, "Product successfully restored"));
  }

  @DeleteProductOperation
  @DeleteMapping("/{productId}")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseEntity<ResponseWrapper<Void>> softDeleteProduct(
      @ProductApiParameters.ProductId @PathVariable @Valid @NotBlank String productId) {

    var productIdObj = ProductID.from(productId);
    commandUseCases.deleteProduct(productIdObj);

    return ResponseEntity.ok(ResponseWrapper.deleted(null, "Product"));
  }
}
