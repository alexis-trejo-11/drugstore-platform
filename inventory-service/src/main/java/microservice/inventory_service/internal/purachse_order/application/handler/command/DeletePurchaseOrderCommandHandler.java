package microservice.inventory_service.internal.purachse_order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.application.command.DeletePurchaseOrderCommand;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DeletePurchaseOrderCommandHandler {
    private final OrderRepository orderRepository;

    @Transactional
    private void handle(DeletePurchaseOrderCommand command) {
        PurchaseOrder order = orderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new IllegalArgumentException("Purchase Order not found with id: " + command.purchaseOrderId()));

        if (command.hardDelete()) {
            order.validateHardDelete();
            orderRepository.delete(command.purchaseOrderId());
        }
        order.softDelete();
        orderRepository.save(order);

    }
}
