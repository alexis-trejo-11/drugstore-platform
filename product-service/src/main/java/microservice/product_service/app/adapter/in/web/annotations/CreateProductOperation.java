package microservice.product_service.app.adapter.in.web.annotations;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import microservice.product_service.app.adapter.in.web.dto.CreateProductRequest;

import java.lang.annotation.*;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Create New Product", description = "Adds a new product to the system with all its details")
@RequestBody(description = "Product details for creation", required = true, content = @Content(mediaType = "application/json", schema = @Schema(implementation = CreateProductRequest.class), examples = @ExampleObject(name = "Example Create Request", value = """
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
    @ApiResponse(responseCode = "201", description = "Product successfully created", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Success Response", value = """
        {
          "success": true,
          "message": "Product created",
          "data": {
            "id": "generated-uuid-here"
          }
        }
        """))),
    @ApiResponse(responseCode = "400", description = "Bad Request - Invalid input data", content = @Content(mediaType = "application/json", schema = @Schema(implementation = libs_kernel.response.ResponseWrapper.class), examples = @ExampleObject(name = "Validation Error", value = """
        {
          "success": false,
          "message": "Validation Failed",
          "data": {
            "name": "Product name is required",
            "price": "Price must be positive"
          }
        }
        """)))
})
public @interface CreateProductOperation {
}
