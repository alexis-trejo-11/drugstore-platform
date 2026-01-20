package microservice.product_service.app.infrastructure.in.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.in.query.GetProductByBarCodeQuery;
import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.query.GetProductBySKUQuery;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.application.port.in.usecase.ProductCommandUseCases;
import microservice.product_service.app.application.port.in.usecase.ProductQueryUseCases;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.infrastructure.in.web.annotations.*;
import microservice.product_service.app.infrastructure.in.web.dto.CreateProductRequest;
import microservice.product_service.app.infrastructure.in.web.dto.ProductResponse;
import microservice.product_service.app.infrastructure.in.web.dto.UpdateProductRequest;
import microservice.product_service.app.infrastructure.in.web.mapper.ProductResponseMapper;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	public ResponseWrapper<ProductResponse> getProductByID(
			@ProductApiParameters.ProductId @PathVariable @Valid @NotBlank String productId) {

		var query = GetProductByIDQuery.of(productId);
		Product product = queryUseCases.getProductByID(query);
		ProductResponse productResponse = responseMapper.toResponse(product);

		return ResponseWrapper.found(productResponse, "Product");
	}

	@GetMapping("/sku/{sku}")
	//@GetProductBySKUOperation
	public ResponseWrapper<ProductResponse> getProductBySKU(
			@PathVariable @Valid @NotBlank String sku) {
		var query = GetProductBySKUQuery.from(sku);
		Product product = queryUseCases.getProductBySKU(query);
		ProductResponse productResponse = responseMapper.toResponse(product);

		return ResponseWrapper.found(productResponse, "Product");
	}

	@GetMapping("/barcode/{barcode}")
	//@GetProductByBarcodeOperation
	public ResponseWrapper<ProductResponse> getProductByBarcode(
			@PathVariable @Valid @NotBlank String barcode) {
		var query = new GetProductByBarCodeQuery(barcode);
		Product product = queryUseCases.getProductByBarcode(query);
		ProductResponse productResponse = responseMapper.toResponse(product);
		return ResponseWrapper.found(productResponse, "Product");
	}

	@CreateProductOperation
	@PostMapping
	public ResponseEntity<ResponseWrapper<ProductID>> createProduct(
			@Valid @RequestBody @NotNull CreateProductRequest productRequest) {

		CreateProductCommand command = productRequest.toCommand();
		Product product = commandUseCases.createProduct(command);
		var response = ResponseWrapper.created(product.getId(), "Product");

		return ResponseEntity.status(HttpStatus.CREATED).body(response);
	}

	@UpdateProductOperation
	@PutMapping("/{productId}")
	public ResponseEntity<ResponseWrapper<Void>> updateProduct(
			@ProductApiParameters.ProductId @PathVariable @Valid @NotBlank String productId,
			@RequestBody @Valid UpdateProductRequest request) {

		UpdateProductCommand command = request.toCommand(productId);
		commandUseCases.updateProduct(command);

		return ResponseEntity.ok(ResponseWrapper.updated(null, "Product"));
	}

	@PatchMapping("/{productId}/restore")
	//@RestoreProductOperation
	public ResponseEntity<ResponseWrapper<Void>> restoreProduct(
			@ProductApiParameters.ProductId @PathVariable @Valid @NotBlank String productId) {

		var productIdObj = ProductID.from(productId);
		commandUseCases.restoreProduct(productIdObj);

		return ResponseEntity.ok(ResponseWrapper.success(null, "Product successfully restored"));
	}

	@DeleteProductOperation
	@DeleteMapping("/{productId}")
	public ResponseEntity<ResponseWrapper<Void>> softDeleteProduct(
			@ProductApiParameters.ProductId @PathVariable @Valid @NotBlank String productId) {

		var productIdObj = ProductID.from(productId);
		commandUseCases.deleteProduct(productIdObj);

		return ResponseEntity.ok(ResponseWrapper.deleted(null, "Product"));
	}
}
