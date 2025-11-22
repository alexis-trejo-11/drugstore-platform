package microservice.inventory_service.order.supplier_purchase.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.order.supplier_purchase.application.command.DeletePurchaseOrderCommand;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class DeletePurchaseOrderCmdHandler {
    private final PurchaseOrderRepository orderRepository;

    @Transactional
    private void handle(DeletePurchaseOrderCommand command) {
        log.info("Handling DeletePurchaseOrderCommand for PurchaseOrderId: {}", command.purchaseOrderId());
        PurchaseOrder order = orderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found with id: " + command.purchaseOrderId()));

        if (command.hardDelete()) {
            log.info("Performing hard delete for PurchaseOrderId: {}", command.purchaseOrderId());
            order.validateHardDelete();

            log.info("Deleting PurchaseOrder with ID: {}", command.purchaseOrderId());
            orderRepository.delete(command.purchaseOrderId());
        } else {
            log.info("Performing soft delete for PurchaseOrderId: {}", command.purchaseOrderId());
            order.softDelete();

            log.info("Saving soft-deleted PurchaseOrder with ID: {}", command.purchaseOrderId());
            orderRepository.save(order);
        }

        log.info("Delete operation completed for PurchaseOrderId: {}", command.purchaseOrderId());
    }
}
