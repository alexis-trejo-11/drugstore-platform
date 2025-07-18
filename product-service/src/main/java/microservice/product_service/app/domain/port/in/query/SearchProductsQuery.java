package microservice.product_service.app.domain.port.in.query;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.product_service.app.domain.model.ProductCategory;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchProductsQuery {
    private String name;
    private ProductCategory category;
    private String manufacturer;
    private Boolean requiresPrescription;
    private Boolean onlyAvailable;
    private int page;
    private int size;
}