package microservice.cart_service.config.exception;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import libs_kernel.config.CustomGlobalExceptionHandler;
import libs_kernel.response.Error;
import libs_kernel.response.ResponseWrapper;
import microservice.cart_service.app.cart.core.domain.exception.CartItemNotFoundException;
import microservice.cart_service.app.cart.core.domain.exception.CartNotFoundException;
import microservice.cart_service.app.cart.core.domain.exception.CartOperationException;
import microservice.cart_service.app.cart.core.domain.exception.CartValidationException;
import microservice.cart_service.app.cart.core.domain.exception.CartValueObjectException;
import microservice.cart_service.app.cart.core.domain.exception.InvalidQuantityException;
import microservice.cart_service.app.cart.core.domain.exception.ProductNotAvailableException;
import microservice.cart_service.app.cart.core.domain.exception.ProductNotFoundException;

/**
 * Global Exception Handler for the Cart Microservice.
 * This class intercepts cart-specific exceptions and translates them
 * into consistent, user-friendly error responses using the ResponseWrapper
 * format.
 * <p>
 * Extends CustomGlobalExceptionHandler to inherit common exception handling.
 * </p>
 */
@RestControllerAdvice
@Order(-1) // Higher priority than shared kernel
public class GlobalExceptionHandler extends CustomGlobalExceptionHandler {

  private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

  /**
   * Handle CartNotFoundException - when a cart is not found by ID or customer.
   */
  @ExceptionHandler(CartNotFoundException.class)
  public ResponseEntity<ResponseWrapper<?>> handleCartNotFoundException(CartNotFoundException ex) {
    log.warn("Cart not found: {}", ex.getMessage());

    Error error = buildCartError("CART_NOT_FOUND", ex.getMessage(), ex);
    ResponseWrapper<?> response = ResponseWrapper.error("Cart Not Found", error);

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * Handle CartItemNotFoundException - when a cart item is not found.
   */
  @ExceptionHandler(CartItemNotFoundException.class)
  public ResponseEntity<ResponseWrapper<?>> handleCartItemNotFoundException(CartItemNotFoundException ex) {
    log.warn("Cart item not found: {}", ex.getMessage());

    Error error = buildCartError("CART_ITEM_NOT_FOUND", ex.getMessage(), ex);
    ResponseWrapper<?> response = ResponseWrapper.error("Cart Item Not Found", error);

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * Handle CartValidationException - general validation errors for cart data.
   */
  @ExceptionHandler(CartValidationException.class)
  public ResponseEntity<ResponseWrapper<?>> handleCartValidationException(CartValidationException ex) {
    log.warn("Cart validation failed: {}", ex.getMessage());

    Error error = buildCartError("CART_VALIDATION_ERROR", ex.getMessage(), ex);
    ResponseWrapper<?> response = ResponseWrapper.error("Cart Validation Error", error);

    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle CartValueObjectException - when value object creation fails.
   */
  @ExceptionHandler(CartValueObjectException.class)
  public ResponseEntity<ResponseWrapper<?>> handleCartValueObjectException(CartValueObjectException ex) {
    log.warn("Cart value object error [{}]: {}", ex.getValueObject(), ex.getMessage());

    Error error = buildCartError("INVALID_VALUE_OBJECT", ex.getMessage(), ex);
    if (ex.getValueObject() != null) {
      error.setDetails(Map.of(
          "valueObject", ex.getValueObject(),
          "message", ex.getMessage()));
    }

    ResponseWrapper<?> response = ResponseWrapper.error("Invalid Cart Value", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle InvalidQuantityException - when quantity is invalid.
   */
  @ExceptionHandler(InvalidQuantityException.class)
  public ResponseEntity<ResponseWrapper<?>> handleInvalidQuantityException(InvalidQuantityException ex) {
    log.warn("Invalid quantity: {}", ex.getMessage());

    Error error = buildCartError("INVALID_QUANTITY", ex.getMessage(), ex);
    error.setDetails(Map.of(
        "field", "quantity",
        "invalidValue", ex.getInvalidQuantity()));

    ResponseWrapper<?> response = ResponseWrapper.error("Invalid Quantity", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Handle ProductNotFoundException - when a product is not found.
   */
  @ExceptionHandler(ProductNotFoundException.class)
  public ResponseEntity<ResponseWrapper<?>> handleProductNotFoundException(ProductNotFoundException ex) {
    log.warn("Product not found: {}", ex.getMessage());

    Error error = buildCartError("PRODUCT_NOT_FOUND", ex.getMessage(), ex);
    ResponseWrapper<?> response = ResponseWrapper.error("Product Not Found", error);

    return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
  }

  /**
   * Handle ProductNotAvailableException - when a product is not available.
   */
  @ExceptionHandler(ProductNotAvailableException.class)
  public ResponseEntity<ResponseWrapper<?>> handleProductNotAvailableException(ProductNotAvailableException ex) {
    log.warn("Product not available: {}", ex.getMessage());

    Error error = buildCartError("PRODUCT_NOT_AVAILABLE", ex.getMessage(), ex);
    if (ex.getProductId() != null) {
      error.setDetails(Map.of(
          "productId", ex.getProductId().toString(),
          "status", "unavailable"));
    }

    ResponseWrapper<?> response = ResponseWrapper.error("Product Not Available", error);
    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
  }

  /**
   * Handle CartOperationException - when a cart operation fails.
   */
  @ExceptionHandler(CartOperationException.class)
  public ResponseEntity<ResponseWrapper<?>> handleCartOperationException(CartOperationException ex) {
    log.warn("Cart operation failed [{}]: {}", ex.getOperation(), ex.getMessage());

    Error error = buildCartError("CART_OPERATION_FAILED", ex.getMessage(), ex);
    if (ex.getOperation() != null) {
      error.setDetails(Map.of("operation", ex.getOperation()));
    }

    ResponseWrapper<?> response = ResponseWrapper.error("Cart Operation Failed", error);
    return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
  }

  /**
   * Build a standardized Error object for cart exceptions.
   */
  private Error buildCartError(String errorCode, String errorMessage, Exception ex) {
    Error error = new Error();
    error.setErrorCode(errorCode);
    error.setErrorMessage(errorMessage);
    return error;
  }
}
