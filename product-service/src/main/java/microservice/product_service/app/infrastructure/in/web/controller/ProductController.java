package microservice.product_service.app.infrastructure.in.web.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.response.ResponseWrapper;
import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.usecase.ProductCommandUseCases;
import microservice.product_service.app.application.port.in.usecase.ProductQueryUseCases;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.enums.ProductCategory;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.infrastructure.in.web.dto.CreateProductRequest;
import microservice.product_service.app.infrastructure.in.web.dto.ProductResponse;
import microservice.product_service.app.infrastructure.in.web.dto.UpdateProductRequest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v2/products")
@Tag(name = "Product Management", description = "Operations related to product creation, retrieval, update, and deletion.")
public class ProductController {

  private final ProductCommandUseCases commandUseCases;
  private final ProductQueryUseCases queryUseCases;

  @Autowired
  public ProductController(ProductCommandUseCases commandUseCases, ProductQueryUseCases queryUseCases) {
    this.commandUseCases = commandUseCases;
    this.queryUseCases = queryUseCases;
  }

  /**
   * Searches for products based on various criteria.
   * Supports filtering by name, category, manufacturer, prescription requirement,
   * and availability.
   * Also supports pagination.
   */
  @Operation(summary = "Search Products", description = "Retrieves a paginated list of products based on provided search criteria such as name, category, manufacturer, prescription requirement, and stock availability.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved products", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), // Base
                                                                                                                                                                                                       // wrapper
          examples = @ExampleObject(name = "Success Response", value = """
              {
                "success": true,
                "message": "Product found",
                "data": [
                  {
                    "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
                    "name": "Paracetamol 500mg",
                    "description": "Tablets for pain and fever relief.",
                    "category": "ANALGESICS",
                    "price": 8.75,
                    "stockQuantity": 2000
                    // ... other product fields
                  }
                ]
              }
              """))),
      @ApiResponse(responseCode = "400", description = "Bad Request - Invalid query parameters or format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Bad Request Example", value = """
          {
            "success": false,
            "message": "Invalid Argument Type",
            "data": "Parameter 'page' has an invalid type. Expected 'int'."
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error - Unexpected server error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Internal Server Error", value = """
          {
            "success": false,
            "message": "Internal Server Error",
            "data": "An unexpected error occurred. Please try again later."
          }
          """)))
  })
  @GetMapping
  public ResponseEntity<ResponseWrapper<List<ProductResponse>>> searchProducts(
      @Parameter(description = "Query parameters for product search and pagination") @ModelAttribute SearchProductsQuery query) {
    if (query.getPage() < 0) {
      query.setPage(0);
    }
    if (query.getSize() <= 0) {
      query.setSize(10);
    }

    List<Product> productList = queryUseCases.searchProducts(query);
    List<ProductResponse> productResponseList = productList.stream()
        .map(ProductResponse::from)
        .toList();

    return ResponseEntity.ok(ResponseWrapper.found(productResponseList, "Product"));
  }

  /**
   * Retrieves a list of all available product categories.
   */
  @Operation(summary = "Get All Product Categories", description = "Retrieves a list of all valid product categories.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved categories", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
          {
            "success": true,
            "message": "Product Categories found",
            "data": ["ANALGESICS", "ANTIBIOTICS", "VITAMINS"]
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
  })
  @GetMapping("/categories")
  public ResponseWrapper<List<String>> getAllProductStatus() {
    List<String> categories = ProductCategory.getAllDisplayNames();
    return ResponseWrapper.found(categories, "Product Categories");
  }

  /**
   * Retrieves a product by its unique identifier.
   */
  @Operation(summary = "Get Product by ID", description = "Retrieves a single product's details using its unique UUID.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Successfully retrieved product", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
          {
            "success": true,
            "message": "Product found",
            "data": {
              "id": "a1b2c3d4-e5f6-7890-1234-567890abcdef",
              "name": "Paracetamol 500mg",
              "description": "Tablets for pain and fever relief.",
              "category": "ANALGESICS",
              "price": 8.75,
              "stockQuantity": 2000
              // ... other product fields
            }
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Not Found Example", value = """
          {
            "success": false,
            "message": "Product Not Found",
            "data": "Product with ID 'non-existent-uuid' not found."
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Bad Request - Invalid UUID format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Bad Request UUID", value = """
          {
            "success": false,
            "message": "Invalid Argument Type",
            "data": "Parameter 'productId' has an invalid type. Expected 'UUID'."
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
  })
  @GetMapping("/{productId}")
  public ResponseWrapper<ProductResponse> getProduct(
      @Parameter(description = "Unique identifier of the product (UUID format)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef") @Valid @PathVariable String productId) {
    var query = GetProductByIDQuery.of(productId);
    Product product = queryUseCases.getProductByID(query);

    var productResponse = ProductResponse.from(product);
    return ResponseWrapper.found(productResponse, "Product");
  }

  /**
   * Creates a new product with the provided details.
   */
  @Operation(summary = "Create New Product", description = "Adds a new product to the system with all its details.")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Product details for creation", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateProductRequest.class), examples = @ExampleObject(name = "Example Create Request", value = """
      {
        "name": "Paracetamol 500mg",
        "description": "Tablets for pain and fever relief.",
        "activeIngredient": "Paracetamol",
        "manufacturer": "HealthMeds Corp.",
        "category": "ANALGESICS",
        "price": 8.75,
        "stockQuantity": 2000,
        "barcode": "9876543210987",
        "batchNumber": "PC-202507-A01",
        "expirationDate": "2026-07-17T12:00:00",
        "manufactureDate": "2025-01-10T09:30:00",
        "requiresPrescription": false,
        "contraindications": [
          "Severe liver impairment",
          "Hypersensitivity to paracetamol"
        ],
        "dosage": "One tablet every 4-6 hours as needed.",
        "administration": "Oral, with water, may be taken with or without food."
      }
      """)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "201", description = "Product successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
          {
            "success": true,
            "message": "Product created",
            "data": {
              "id": "generated-uuid-here",
              "name": "Paracetamol 500mg",
              // ... other product fields
            }
          }
          """))),
      @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data or validation errors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Validation Error", value = """
          {
            "success": false,
            "message": "Validation Failed",
            "data": {
              "name": "Product name is required",
              "price": "Price must be positive"
            }
          }
          """))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
  })
  @PostMapping
  public ResponseEntity<ResponseWrapper<ProductResponse>> createProduct(@Valid @RequestBody CreateProductRequest productRequest) {
    CreateProductCommand command = productRequest.toCommand();
    Product product = commandUseCases.createProduct(command);

    var response = ResponseWrapper.created(ProductResponse.from(product), "Product");
    return ResponseEntity.status(HttpStatus.CREATED).body(response);
  }

  /**
   * Updates an existing product identified by its ID.
   * Allows for partial updates of product details.
   */
  @Operation(summary = "Update Product", description = "Updates an existing product's details using its unique UUID. Supports partial updates (only provided fields will be modified).")
  @io.swagger.v3.oas.annotations.parameters.RequestBody(description = "Product details to update. Fields omitted or null will not be changed.", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = UpdateProductRequest.class), examples = @ExampleObject(name = "Example Update Request (Partial)", value = """
      {
        "name": "Updated Paracetamol",
        "price": 9.50,
        "stockQuantity": 1800
      }
      """)))
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product successfully updated", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
          {
            "success": true,
            "message": "Product updated",
            "data": {
              "id": "provided-uuid-here",
              "name": "Updated Paracetamol",
              "price": 9.50,
              "stockQuantity": 1800
              // ... other product fields, some potentially unchanged
            }
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data, UUID format, or validation errors", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
  })
  @PutMapping("/{productId}")
  public ResponseEntity<ResponseWrapper<ProductResponse>> updateProduct(
      @Parameter(description = "Unique identifier of the product to update (UUID format)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef") @Valid @PathVariable UUID productId,
      @Valid @RequestBody UpdateProductRequest request) {

    UpdateProductCommand command = request.toCommand(ProductID.from(productId));
    Product product = commandUseCases.updateProduct(command);
    return ResponseEntity.ok(ResponseWrapper.updated(ProductResponse.from(product), "Product"));
  }

  /**
   * Soft deletes a product by setting its status to inactive or similar.
   * The product is not removed from the database but marked as unavailable.
   */
  @Operation(summary = "Soft Delete Product", description = "Marks a product as inactive or deleted in the system without permanently removing it from the database.")
  @ApiResponses(value = {
      @ApiResponse(responseCode = "200", description = "Product successfully soft-deleted", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
          {
            "success": true,
            "message": "Product deleted",
            "data": null
          }
          """))),
      @ApiResponse(responseCode = "404", description = "Product not found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
      @ApiResponse(responseCode = "400", description = "Bad Request - Invalid UUID format", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
      @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
  })
  @DeleteMapping("/{productId}")
  public ResponseEntity<ResponseWrapper<Void>> softDeleteProduct(
      @Parameter(description = "Unique identifier of the product to soft delete (UUID format)", example = "a1b2c3d4-e5f6-7890-1234-567890abcdef") @Valid @PathVariable String productId) {
    commandUseCases.deleteProduct(ProductID.from(productId));
    return ResponseEntity.ok(ResponseWrapper.deleted(null,"Product"));
  }
}
