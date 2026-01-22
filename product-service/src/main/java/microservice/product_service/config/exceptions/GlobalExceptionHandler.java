package microservice.product_service.config.exceptions;

import libs_kernel.config.CustomGlobalExceptionHandler;
import libs_kernel.response.Error;
import libs_kernel.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.app.domain.exception.*;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Global Exception Handler for the Product Microservice.
 * This class intercepts product-specific exceptions and translates them
 * into consistent, user-friendly error responses using the ResponseWrapper
 * format.
 * <p>
 * Extends CustomGlobalExceptionHandler to inherit common exception handling.
 * </p>
 */
@RestControllerAdvice
@Slf4j
@Order(0) // Higher priority than parent handler
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler {

  // ==================== PRODUCT NOT FOUND ====================

  /**
   * Handle ProductNotFoundException - when a product is not found by ID, SKU, or
   * barcode.
   */
  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ResponseWrapper<?>> handleProductNotFoundException(ProductNotFoundException ex) {
    log.warn("Product not found: {}", ex.getMessage());

    Error error = buildProductError("PRODUCT_NOT_FOUND", ex.getMessage(), ex);
    ResponseWrapper<?> response = ResponseWrapper.error("Product Not Found", error);

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  // ==================== PRODUCT VALIDATION EXCEPTIONS ====================

  /**
   * Handle ProductValidationException - general validation errors for product
   * data.
   */
  @ExceptionHandler(ProductValidationException.class)
  public ResponseEntity<ResponseWrapper<?>> handleProductValidationException(ProductValidationException ex) {
    log.warn("Product validation failed: {}", ex.getMessage());

    Error error = buildProductError("PRODUCT_VALIDATION_ERROR", ex.getMessage(), ex);
    ResponseWrapper<?> response = ResponseWrapper.error("Product Validation Error", error);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle InvalidProductDataException - when product data is structurally
   * invalid.
   */
  @ExceptionHandler(InvalidProductDataException.class)
  public ResponseEntity<ResponseWrapper<?>> handleInvalidProductDataException(InvalidProductDataException ex) {
    log.warn("Invalid product data: {}", ex.getMessage());

    Error error = buildProductError("INVALID_PRODUCT_DATA", ex.getMessage(), ex);
    ResponseWrapper<?> response = ResponseWrapper.error("Invalid Product Data", error);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle ProductValueObjectException - when value object creation fails.
   */
  @ExceptionHandler(ProductValueObjectException.class)
  public ResponseEntity<ResponseWrapper<?>> handleProductValueObjectException(ProductValueObjectException ex) {
    log.warn("Product value object error [{}]: {}", ex.getValueObject(), ex.getMessage());

    Error error = buildProductError("INVALID_VALUE_OBJECT", ex.getMessage(), ex);
    if (ex.getValueObject() != null) {
      error.setDetails(Map.of(
          "valueObject", ex.getValueObject(),
          "message", ex.getMessage()));
    }

    ResponseWrapper<?> response = ResponseWrapper.error("Invalid Product Value", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // ==================== PRODUCT CONFLICT EXCEPTIONS ====================

  /**
   * Handle ProductConflictException - when there's a conflict (e.g., duplicate
   * SKU/barcode).
   */
  @ExceptionHandler(ProductConflictException.class)
  public ResponseEntity<ResponseWrapper<?>> handleProductConflictException(ProductConflictException ex) {
    log.warn("Product conflict: {}", ex.getMessage());

    Error error = buildProductError("PRODUCT_CONFLICT", ex.getMessage(), ex);
    ResponseWrapper<?> response = ResponseWrapper.error("Product Conflict", error);

    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  // ==================== PRODUCT PRICE EXCEPTIONS ====================

  /**
   * Handle InvalidPriceException - when product price is invalid.
   */
  @ExceptionHandler(InvalidPriceException.class)
  public ResponseEntity<ResponseWrapper<?>> handleInvalidPriceException(InvalidPriceException ex) {
    log.warn("Invalid product price: {}", ex.getMessage());

    Error error = buildProductError("INVALID_PRICE", ex.getMessage(), ex);
    error.setDetails(Map.of("field", "price"));

    ResponseWrapper<?> response = ResponseWrapper.error("Invalid Price", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // ==================== PRODUCT DATE EXCEPTIONS ====================

  /**
   * Handle InvalidExpirationDateException - when expiration date is invalid.
   */
  @ExceptionHandler(InvalidExpirationDateException.class)
  public ResponseEntity<ResponseWrapper<?>> handleInvalidExpirationDateException(InvalidExpirationDateException ex) {
    log.warn("Invalid expiration date: {}", ex.getMessage());

    Error error = buildProductError("INVALID_EXPIRATION_DATE", ex.getMessage(), ex);
    error.setDetails(Map.of("field", "expirationDate"));

    ResponseWrapper<?> response = ResponseWrapper.error("Invalid Expiration Date", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle InvalidManufactureDateException - when manufacture date is invalid.
   */
  @ExceptionHandler(InvalidManufactureDateException.class)
  public ResponseEntity<ResponseWrapper<?>> handleInvalidManufactureDateException(InvalidManufactureDateException ex) {
    log.warn("Invalid manufacture date: {}", ex.getMessage());

    Error error = buildProductError("INVALID_MANUFACTURE_DATE", ex.getMessage(), ex);
    error.setDetails(Map.of("field", "manufactureDate"));

    ResponseWrapper<?> response = ResponseWrapper.error("Invalid Manufacture Date", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  // ==================== PRESCRIPTION EXCEPTIONS ====================

  /**
   * Handle PrescriptionRequiredException - when prescription is required but not
   * provided.
   */
  @ExceptionHandler(PrescriptionRequiredException.class)
  public ResponseEntity<ResponseWrapper<?>> handlePrescriptionRequiredException(PrescriptionRequiredException ex) {
    log.warn("Prescription required: {}", ex.getMessage());

    Error error = buildProductError("PRESCRIPTION_REQUIRED", ex.getMessage(), ex);
    error.setDetails(Map.of(
        "requiresPrescription", "true",
        "message", "This product requires a valid prescription"));

    ResponseWrapper<?> response = ResponseWrapper.error("Prescription Required", error);
    return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
  }

  // ==================== HELPER METHODS ====================

  /**
   * Build a standardized Error object for product exceptions.
   */
  private Error buildProductError(String errorCode, String errorMessage, Exception ex) {
    Error error = new Error();
    error.setErrorCode(errorCode);
    error.setErrorMessage(errorMessage);
    error.setErrorType(ex.getClass().getSimpleName());
    return error;
  }
}
