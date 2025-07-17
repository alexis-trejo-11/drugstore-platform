package microservice.product_service.app.domain.model;

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

    public String getDisplayName() {
        return displayName;
    }
}