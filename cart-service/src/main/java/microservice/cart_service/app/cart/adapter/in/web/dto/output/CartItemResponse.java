package microservice.cart_service.app.cart.adapter.in.web.dto.output;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Builder;

import microservice.cart_service.app.cart.core.application.dto.ProductSummaryResponse;
import microservice.cart_service.app.product.core.domain.Product;

@Builder
public record CartItemResponse(
    ProductSummaryResponse productSummary,
    int quantity,
    BigDecimal itemTotalPrice,
    LocalDateTime addedAt,
    LocalDateTime updatedAt) {

  public CartItemResponse addProductData(Product product) {
    var productSummaryVO = new ProductSummaryResponse(
        product.getId() != null ? product.getId().toString() : null,
        product.getName(),
        product.getUnitPrice());

    return new CartItemResponse(
        productSummaryVO,
        this.quantity,
        this.itemTotalPrice,
        this.addedAt,
        this.updatedAt);
  }

}
