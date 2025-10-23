package microservice.inventory_service.inventory.application.service;

import libs_kernel.page.PageResponse;
import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.application.handler.query.*;
import microservice.inventory_service.inventory.application.query.GetExpiringBatchesQuery;
import microservice.inventory_service.inventory.application.query.GetInventoryByIdQuery;
import microservice.inventory_service.inventory.application.query.GetInventoryByMedicineQuery;
import microservice.inventory_service.inventory.application.query.GetInventoryMovementsQuery;
import microservice.inventory_service.inventory.domain.entity.Inventory;
import microservice.inventory_service.inventory.domain.entity.InventoryBatch;
import microservice.inventory_service.inventory.domain.entity.InventoryMovement;
import microservice.inventory_service.stock.application.query.GetLowStockInventoriesQuery;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class InventoryQueryService {
    private final GetExpiringBatchesQueryHandler getExpiringBatchesQueryHandler;
    private final GetInventoryByMedicineQueryHandler getInventoryByMedicineQueryHandler;
    private final GetInventoryMovementsQueryHandler getInventoryMovementsQueryHandler;
    private final GetLowStockInventoriesQueryHandler getLowStockInventoriesQueryHandler;
    private final GetInventoryByIdQueryHandler getInventoryByIdQueryHandler;


    public Inventory getInventoryById(GetInventoryByIdQuery query) {
        return getInventoryByIdQueryHandler.handle(query);
    }

    public Inventory getInventoryByMedicine(GetInventoryByMedicineQuery query) {
        return getInventoryByMedicineQueryHandler.handle(query);
    }

    public PageResponse<Inventory> getLowStockInventories(GetLowStockInventoriesQuery query) {
        return getLowStockInventoriesQueryHandler.handle(query);
    }

    public PageResponse<InventoryMovement> getInventoryMovements(GetInventoryMovementsQuery query) {
        return getInventoryMovementsQueryHandler.handle(query);
    }

    public List<InventoryBatch> getExpiringInventoryBatches(GetExpiringBatchesQuery query) {
        return getExpiringBatchesQueryHandler.handle(query);
    }


}
