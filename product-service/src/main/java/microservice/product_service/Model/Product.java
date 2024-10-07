package microservice.product_service.Model;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.ProductPresentation;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.ProductType;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.Enums.RouteOfAdministration;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import jakarta.persistence.*;
import org.hibernate.annotations.Type;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data
@NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "image")
    private String image;

    @Column(name = "price")
    private BigDecimal price;

    @Column(name = "upc")
    private String upc;

    @Column(name = "content")
    private String content;

    @Column(name = "package_dimension")
    private String packageDimension;

    @Column(name = "route_of_administration")
    @Enumerated(EnumType.STRING)
    private RouteOfAdministration routeOfAdministration;

    @Column(name = "product_type")
    @Enumerated(EnumType.STRING)
    private ProductType productType;

    @Column(name = "product_presentation")
    @Enumerated(EnumType.STRING)
    private ProductPresentation productPresentation;

    @Column(name = "prescription_required")
    private boolean prescriptionRequired;

    @Column(name = "age_usage")
    private String ageUsage;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "brand_id")
    private Brand brand;
}