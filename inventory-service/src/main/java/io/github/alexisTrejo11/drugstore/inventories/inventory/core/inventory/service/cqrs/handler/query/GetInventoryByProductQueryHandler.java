package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.handler.query;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.service.cqrs.query.GetInventoryByProductQuery;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.Inventory;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.exception.InventoryNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.port.InventoryRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class GetInventoryByProductQueryHandler {
    private final InventoryRepository inventoryRepository;

    @Transactional(readOnly = true)
    public Inventory handle(GetInventoryByProductQuery query) {
        return inventoryRepository.findByProductId(query.productId())
                .orElseThrow(() -> new InventoryNotFoundException("Inventory not found for product: " + query.productId()));
    }
}
