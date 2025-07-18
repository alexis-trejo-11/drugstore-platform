package microservice.product_service.app.infrastructure.in.web.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ProductCategory;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.port.in.command.CreateProductCommand;
import microservice.product_service.app.domain.port.in.command.UpdateProductCommand;
import microservice.product_service.app.domain.port.in.query.SearchProductsQuery;
import microservice.product_service.app.domain.port.in.usecase.ProductUseCases;
import microservice.product_service.app.infrastructure.in.web.dto.CreateProductRequest;
import microservice.product_service.app.infrastructure.in.web.dto.ProductResponse;
import microservice.product_service.app.infrastructure.in.web.dto.UpdateProductRequest;
import microservice.product_service.app.shared.ResponseWrapper;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/products")
public class ProductController {
    private final ProductUseCases useCases;

    @GetMapping("/")
    public ResponseEntity<ResponseWrapper<List<ProductResponse>>> searchProducts(
            @ModelAttribute SearchProductsQuery query
    ) {
        if (query.getPage() < 0) {
            query.setPage(0);
        }
        if (query.getSize() <= 0) {
            query.setSize(10);
        }

        List<Product> productList = useCases.searchProducts(query);
        List<ProductResponse> productResponseList = productList.stream()
                .map(ProductResponse::from)
                .toList();

        return ResponseEntity.ok(ResponseWrapper.found(productResponseList, "Product"));
    }

    @GetMapping("/categories")
    public ResponseWrapper<List<String>> getAllProductStatus() {
        List<String> categories = ProductCategory.getAllNames();
        return ResponseWrapper.found(categories, "Product Categories");
    }

    @GetMapping("/{productId}")
    public ResponseWrapper<ProductResponse> getProduct(@Valid @PathVariable UUID productId) {
        Product product = useCases.getProduct(ProductId.from(productId));
        ProductResponse productResponse = ProductResponse.from(product);
        return ResponseWrapper.found(productResponse, "Product");
    }

    @PostMapping("/")
    public ResponseEntity<ResponseWrapper<ProductResponse>> createProduct(
            @Valid @RequestBody CreateProductRequest productRequest
    ) {
        CreateProductCommand command = CreateProductCommand.builder()
                .name(productRequest.getName())
                .description(productRequest.getDescription())
                .activeIngredient(productRequest.getActiveIngredient())
                .manufacturer(productRequest.getManufacturer())
                .category(productRequest.getCategory())
                .price(productRequest.getPrice())
                .stockQuantity(productRequest.getStockQuantity())
                .barcode(productRequest.getBarcode())
                .batchNumber(productRequest.getBatchNumber())
                .expirationDate(productRequest.getExpirationDate())
                .manufactureDate(productRequest.getManufactureDate())
                .requiresPrescription(productRequest.isRequiresPrescription())
                .contraindications(productRequest.getContraindications())
                .dosage(productRequest.getDosage())
                .administration(productRequest.getAdministration())
                .build();

        Product product = useCases.createProduct(command);
        ProductResponse productResponse = ProductResponse.from(product);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ResponseWrapper.created(productResponse, "Product"));
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ResponseWrapper<ProductResponse>> updateProduct(
            @Valid @RequestBody UpdateProductRequest request,
            @Valid @PathVariable UUID productId) {

        UpdateProductCommand command = UpdateProductCommand.builder()
                .productId(ProductId.from(productId))
                .name(request.getName())
                .description(request.getDescription())
                .activeIngredient(request.getActiveIngredient())
                .manufacturer(request.getManufacturer())
                .category(request.getCategory())
                .price(request.getPrice())
                .stockQuantity(request.getStockQuantity())
                .barcode(request.getBarcode())
                .batchNumber(request.getBatchNumber())
                .expirationDate(request.getExpirationDate())
                .manufactureDate(request.getManufactureDate())
                .requiresPrescription(request.getRequiresPrescription() != null ? request.getRequiresPrescription() : false)
                .contraindications(request.getContraindications())
                .dosage(request.getDosage())
                .administration(request.getAdministration())
                .build();

        Product product = useCases.updateProduct(command);
        ProductResponse productResponse = ProductResponse.from(product);
        return ResponseEntity.ok(ResponseWrapper.update(productResponse, "Product"));
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<ResponseWrapper<Void>> softDeleteProduct(@Valid @PathVariable String productId) {
        useCases.deleteProduct(ProductId.from(productId));
        return ResponseEntity.ok(ResponseWrapper.deleted("Product"));
    }
}
