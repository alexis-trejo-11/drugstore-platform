package microservice.inventory_service.inventory.core.inventory.domain.entity.valueobject;

public record WareHouseLocation(
        String aisle,
        String shelf,
        String bin,
        String zone
) {

    public String getFullLocation() {
        return String.format("%s-%s-%s-%s", zone, aisle, shelf, bin);
    }
}
