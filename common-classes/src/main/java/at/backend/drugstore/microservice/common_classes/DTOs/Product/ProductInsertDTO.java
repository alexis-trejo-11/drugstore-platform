package at.backend.drugstore.microservice.common_classes.DTOs.Product;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.ProductPresentation;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.ProductType;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.RouteOfAdministration;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductInsertDTO {
    @JsonProperty("name")
    @NotNull(message = "Name Can Not Be Null") @NotBlank(message = "Name Can Not Be Blank")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("price")
    @NotNull @Positive(message = "Price must be Positive")
    private BigDecimal price;

    @JsonProperty("upc")
    private String upc;

    @JsonProperty("content")
    private String content;

    @JsonProperty("package_dimension")
    private String packageDimension;

    @JsonProperty("route_of_administration")
    @Enumerated(EnumType.STRING)
    private RouteOfAdministration routeOfAdministration;

    @JsonProperty("product_presentation")
    @Enumerated(EnumType.STRING)
    private ProductPresentation productPresentation;

    @JsonProperty("product_type")
    @NotNull(message = "Product Type Can Not Be Null")
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @JsonProperty("prescription_required")
    private boolean prescriptionRequired;

    @JsonProperty("age_usage")
    private String ageUsage;

    @JsonProperty("category_id")
    @NotNull(message = "category_id it's obligatory")
    private Long categoryId;

    @JsonProperty("subcategory_id")
    @NotNull(message = "subcategory_id it's obligatory")
    private Long subcategoryId;

    @JsonProperty("supplier_id")
    @NotNull(message = "supplier_id it's obligatory")
    private Long supplierId;

    @JsonProperty("main_category_id")
    @NotNull(message = "main_category_id it's obligatory")
    private Long mainCategoryId;

    @JsonProperty("brand_id")
    @NotNull(message = "brand_id it's obligatory")
    private Long brandId;


    public ProductRelationsIDs getRelationIDs() {
        return new ProductRelationsIDs(this.mainCategoryId, this.categoryId, this.subcategoryId, this.brandId, this.supplierId);
    }

}