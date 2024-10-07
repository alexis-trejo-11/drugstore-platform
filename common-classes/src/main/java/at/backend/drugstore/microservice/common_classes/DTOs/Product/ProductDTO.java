package at.backend.drugstore.microservice.common_classes.DTOs.Product;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.ProductPresentation;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.ProductType;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.RouteOfAdministration;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductDTO {

    @JsonProperty("id")
    private Long id;

    @JsonProperty("name")
    private String name;

    @JsonProperty("image")
    private String image;

    @JsonProperty("price")
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
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @JsonProperty("prescription_required")
    private boolean prescriptionRequired;

    @JsonProperty("age_usage")
    private String ageUsage;

    @JsonProperty("category")
    private String category;

    @JsonProperty("subcategory")
    private String subcategory;

    @JsonProperty("supplier")
    private String supplier;

    @JsonProperty("main_category")
    private String mainCategory;

    @JsonProperty("brand")
    private String brand;


}