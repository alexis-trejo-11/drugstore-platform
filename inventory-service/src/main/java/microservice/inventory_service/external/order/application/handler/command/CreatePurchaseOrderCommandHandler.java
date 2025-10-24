package microservice.inventory_service.external.order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.command.CreatePurchaseOrderCommand;
import microservice.inventory_service.external.order.application.command.PurchaseOrderItemCommand;
import microservice.inventory_service.external.order.domain.entity.*;
import microservice.inventory_service.external.order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.external.order.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class CreatePurchaseOrderCommandHandler {

    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public PurchaseOrderId handle(CreatePurchaseOrderCommand command) {
        List<PurchaseOrderItem> items = command.items().stream()
                .map(this::createPurchaseOrderItem)
                .toList();

        PurchaseOrder purchaseOrder = PurchaseOrder.create(
                command.supplierId(),
                command.supplierName(),
                items,
                command.expectedDeliveryDate(),
                command.deliveryLocation(),
                command.createdBy()
        );

        PurchaseOrder savedOrder = purchaseOrderRepository.save(purchaseOrder);
        return savedOrder.getId();
    }

    private PurchaseOrderItem createPurchaseOrderItem(PurchaseOrderItemCommand itemCommand) {
        return PurchaseOrderItem.create(
                itemCommand.medicineId(),
                itemCommand.medicineName(),
                itemCommand.quantity(),
                itemCommand.unitCost()
        );
    }
}