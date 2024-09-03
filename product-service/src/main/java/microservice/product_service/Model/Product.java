package microservice.product_service.Model;


import lombok.Data;
import lombok.NoArgsConstructor;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "products")
@Data @NoArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String image;

    private BigDecimal price;

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

    @ManyToOne
    @JoinColumn(name = "main_category_id")
    private MainCategory mainCategory;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "subcategory_id")
    private Subcategory subcategory;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;



    public enum ProductPresentation {
        BOX,
        BOTTLE,
        TUBE,
        BLISTER,
        SACHET,
        VIAL,
        JAR,
        AMPOULE,
        SPRAY,
        DROPPER,
        CARTON,
        BAG,
        POUCH,
        CANISTER,
        SYRINGE,
        PATCH,
        INHALER,
        DOSE_PACK,
        KIT,
        STICK
    }

    public enum RouteOfAdministration {
        ORAL,
        SPREAD,
        TOPICAL,
        TRANSDERMAL,
        INTRAMUSCULAR,
        INTRAVENOUS,
        SUBCUTANEOUS,
        RECTAL,
        VAGINAL,
        OPHTHALMIC,
        OTIC,
        NASAL,
        INHALATION,
        BUCCAL,
        SUBLINGUAL
    }

    public enum ProductType {
        CREAM,
        PILL,
        TABLET,
        CAPSULE,
        SYRUP,
        OINTMENT,
        GEL,
        DROPS,
        INJECTION,
        INHALER,
        PATCH,
        SUPPOSITORY,
        LOZENGE,
        POWDER,
        LIQUID,
        SUSPENSION,
        EMULSION,
        SOLUTION,
        GRANULES,
        SPRAY,
        FOAM,
        SHAMPOO,
        SOAP,
        TINCTURE,
        PASTE,
        BANDAGE,
        BALM,
        SUPPLEMENT,
        VITAMIN,
        HERBAL,
        TOPICAL,
        ORAL,
        RECTAL,
        VAGINAL,
        NASAL,
        AEROSOL,
        CHEWABLE,
        EFFERVESCENT,
        TRANSDERMAL,
        GELCAP,
        MOUTHWASH,
        GROCERIES,
        DIAPER,
        DISPOSABLE_DIAPER,
        CONDOM,
        PROPHYLACTIC,
        PREGNANCY_TEST,
        PREGNANCY_TEST_KIT
    }



}
