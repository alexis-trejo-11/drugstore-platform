package microservice.product_service.app.domain.model;

import lombok.Getter;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Getter
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
    MEDICAL_DEVICES("Medical Devices");

    private final String displayName;

    ProductCategory(String displayName) {
        this.displayName = displayName;
    }

    public static List<String> getAllNames() {
        return Arrays.stream(ProductCategory.values())
                .map(ProductCategory::getDisplayName)
                .toList();
    }
}