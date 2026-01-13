package microservice.inventory_service.shared.exception;

import microservice.inventory_service.inventory.core.inventory.domain.exception.base.InventoryException;

public class MissingItemException extends InventoryException {
    public MissingItemException(String className, String itemName) {
        super("Missing item: " + itemName + " in class: " + className, "MISSING_ITEM_ERROR");
    }
}
