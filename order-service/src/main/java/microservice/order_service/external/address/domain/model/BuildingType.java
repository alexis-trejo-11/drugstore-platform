package microservice.order_service.external.address.domain.model;

import lombok.Getter;

@Getter
public enum BuildingType {
    HOUSE ("House"),
    APARTMENT ("Apartment"),
    OFFICE ("Office"),
    Condominium ("Condominium"),
    WAREHOUSE ("Warehouse"),
    COMMERCIAL ("Commercial"),
    OTHER ("Other");

    BuildingType(String displayName) {
    }

    public static BuildingType fromString(String value) {
        for (BuildingType type : BuildingType.values()) {
            if (type.name().equalsIgnoreCase(value)) {
                return type;
            }
        }
        return OTHER;
    }
}