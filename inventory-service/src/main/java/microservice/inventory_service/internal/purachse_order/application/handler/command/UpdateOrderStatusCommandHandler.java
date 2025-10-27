package microservice.inventory_service.internal.purachse_order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.purachse_order.application.command.UpdateOrderStatusCommand;
import microservice.inventory_service.internal.purachse_order.domain.entity.PurchaseOrder;
import microservice.inventory_service.internal.purachse_order.domain.exception.OrderNotFoundException;
import microservice.inventory_service.internal.purachse_order.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdateOrderStatusCommandHandler {
    private final OrderRepository orderRepository;

    @Transactional
    public void handle(UpdateOrderStatusCommand command) {
        PurchaseOrder PurchaseOrder = orderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));

        var newStatus = command.newStatus();
        switch (newStatus) {
            case APPROVED:
                PurchaseOrder.approve(command.updatedBy());
                break;
            case SENT:
                PurchaseOrder.supplierSending();
                break;
            case CANCELLED:
                PurchaseOrder.cancel();
                break;
            case REJECTED:
                PurchaseOrder.reject();
                break;
            default:
                throw new IllegalArgumentException("Not supported status: " + newStatus);
        }

        orderRepository.save(PurchaseOrder);
    }
}