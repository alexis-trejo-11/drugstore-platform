package microservice.purchase.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory.domain.port.output.InventoryBatchRepository;
import microservice.inventory.domain.port.output.InventoryMovementRepository;
import microservice.inventory.domain.port.output.InventoryRepository;
import microservice.purchase.application.command.ReceivePurchaseOrderCommand;
import microservice.purchase.domain.entity.PurchaseOrder;
import microservice.purchase.domain.exception.PurchaseOrderNotFoundException;
import microservice.purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class ReceivePurchaseOrderCommandHandler {

    private final PurchaseOrderRepository purchaseOrderRepository;
    private final InventoryRepository inventoryRepository;
    private final InventoryBatchRepository batchRepository;
    private final InventoryMovementRepository movementRepository;

    @Transactional
    public void handle(ReceivePurchaseOrderCommand command) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase order not found"));

        purchaseOrder.receiveItems(command.toReceivedItems(), command.receivedDate());
    }
}