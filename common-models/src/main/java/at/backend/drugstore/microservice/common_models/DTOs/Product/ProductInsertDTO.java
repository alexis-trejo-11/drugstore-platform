package at.backend.drugstore.microservice.common_models.DTOs.Product;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
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
    private String routeOfAdministration;

    @JsonProperty("product_presentation")
    private String productPresentation;

    @JsonProperty("product_type")
    @NotNull(message = "Product Type Can Not Be Null") @NotBlank(message = "Product Type Can Not Be Blank")
    private String productType;

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

}