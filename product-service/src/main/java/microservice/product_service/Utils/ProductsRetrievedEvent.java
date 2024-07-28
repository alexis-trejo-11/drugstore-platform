package microservice.product_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import lombok.Getter;

import java.util.List;

@Getter
public class ProductsRetrievedEvent {
    private final List<ProductDTO> products;

    public ProductsRetrievedEvent(List<ProductDTO> products) {
        this.products = products;
    }

}