package microservice.product_service.app.domain.model.enums;


import java.util.Arrays;
import java.util.List;


public enum ProductCategory {
    ANALGESICS("Analgesics"),
    ANTIBIOTICS("Antibiotics"),
    VITAMINS("Vitamins"),
    DERMATOLOGY("Dermatology"),
    CARDIOLOGY("Cardiology"),
    RESPIRATORY("Respiratory"),
    DIGESTIVE("Digestive"),
    PEDIATRIC("Pediatric"),
    GENERIC("Generic"),
    COSMETICS("Cosmetics"),
    PERSONAL_CARE("Personal Care"),
    MEDICAL_DEVICES("Medical Devices"),
    NOT_CATEGORIZED("Not Categorized");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public static List<String> getAllDisplayNames() {
        return Arrays.stream(ProductCategory.values())
                .map(ProductCategory::getDisplayName)
                .toList();
    }

    public String getDisplayName() {
        return displayName;
    }
}