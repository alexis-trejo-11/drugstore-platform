package microservice.purchase.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.purchase.application.command.CreatePurchaseOrderCommand;
import microservice.purchase.application.command.PurchaseOrderItemCommand;
import microservice.purchase.domain.entity.*;
import microservice.purchase.domain.port.output.PurchaseOrderRepository;
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

        PurchaseOrder purchaseOrder = PurchaseOrderFactory.create(
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
        return PurchaseOrderItemFactory.create(
                itemCommand.medicineId(),
                itemCommand.medicineName(),
                itemCommand.quantity(),
                itemCommand.unitCost()
        );
    }
}