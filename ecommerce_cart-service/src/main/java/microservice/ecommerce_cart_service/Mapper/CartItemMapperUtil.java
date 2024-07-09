package microservice.ecommerce_cart_service.Mapper;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;

import java.math.BigDecimal;

public class CartItemMapperUtil {

    public static BigDecimal calculateItemTotal(ProductDTO productDTO, int quantity) {
        return productDTO.getPrice().multiply(BigDecimal.valueOf(quantity));
    }
}