package microservice.inventory_service.internal.purachse_order.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.internal.purachse_order.application.command.InsertOrderCommand;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrderItem;
import microservice.inventory_service.internal.purachse_order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class CreatePurchaseOrderCommandHandler {
    private final OrderRepository orderRepository;

    @Transactional
    public PurchaseOrderId handle(InsertOrderCommand command) {
        log.info("Handling CreatePurchaseOrderCommand for PurchaseOrderId: {}", command.purchaseOrderId());
        PurchaseOrder purchaseOrder = commandToDomain(command);

        if (command.isUpdate()) {
          validateUpdate(command);
        } else {
            log.info("Create operation detected for PurchaseOrderId: {}", command.purchaseOrderId());
        }

        log.info("Saving PurchaseOrder with ID: {}", command.purchaseOrderId());
        PurchaseOrder savedPurchaseOrder = orderRepository.save(purchaseOrder);

        log.info("PurchaseOrder with ID: {} saved successfully", savedPurchaseOrder.getId());
        return savedPurchaseOrder.getId();
    }

    private PurchaseOrder commandToDomain(InsertOrderCommand command) {
        log.info("Creating PurchaseOrderItems for PurchaseOrderId: {}", command.purchaseOrderId());
        List<PurchaseOrderItem> items = command.items().stream()
                .map(itemCommand -> PurchaseOrderItem.create(
                        itemCommand.id(),
                        itemCommand.productId(),
                        itemCommand.productName(),
                        itemCommand.quantity(),
                        itemCommand.unitCost()
                ))
                .toList();

        log.info("Creating PurchaseOrder entity for PurchaseOrderId: {}", command.purchaseOrderId());
        return PurchaseOrder.create(
                command.purchaseOrderId(),
                command.supplierId(),
                command.supplierName(),
                items,
                command.expectedDeliveryDate(),
                command.deliveryLocation(),
                command.createdBy(),
                command.currency()
        );
    }

    private void validateUpdate(InsertOrderCommand command) {
        log.info("Update operation detected for PurchaseOrderId: {}", command.purchaseOrderId());
        var existingOrder = orderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new IllegalArgumentException("PurchaseOrder with ID " + command.purchaseOrderId() + " does not exist."));

        log.info("Validating update for PurchaseOrderId: {}", command.purchaseOrderId());
        existingOrder.validateUpdate();
    }
}