package at.backend.drugstore.microservice.common_classes.DTOs.Product;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class ProductRelationsIDs {
    private Long mainCategoryId;
    private Long categoryId;
    private Long subcategoryId;
    private Long brandId;
    private Long supplierId;
}
