package microservice.product_service.app.domain.port.in;

import lombok.Builder;
import lombok.Data;
import microservice.product_service.app.domain.model.ProductCategory;

@Data
@Builder
public class SearchProductsQuery {
    private String name;
    private ProductCategory category;
    private String manufacturer;
    private Boolean requiresPrescription;
    private Boolean onlyAvailable;
    private int page;
    private int size;
}