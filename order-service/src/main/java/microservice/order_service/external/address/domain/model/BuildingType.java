package microservice.order_service.external.address.domain.model;

public enum BuildingType {
    HOUSE ("House"),
    APARTMENT ("Apartment"),
    OFFICE ("Office"),
    CONDOMINIUM("CONDOMINIUM"),
    WAREHOUSE ("Warehouse"),
    COMMERCIAL ("Commercial"),
    OTHER ("Other");

    private final String displayName;

    BuildingType(String displayName) {
        this.displayName = displayName;
    }

    public static BuildingType fromString(String value) {
        for (BuildingType type : BuildingType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return OTHER;
    }

    public String getDisplayName() {
        return displayName;
    }


    @Override
    public String toString() {
        return displayName;
    }
}