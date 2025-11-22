package microservice.inventory_service.order.supplier_purchase.application.handler.command;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.inventory_service.order.supplier_purchase.application.command.UpdatePurchaseOrderStatusCommand;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.exception.OrderNotFoundException;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
@Slf4j
public class UpdatePurchaseOrderStatusCmdHandler {
    private final PurchaseOrderRepository orderRepository;

    @Transactional
    public void handle(UpdatePurchaseOrderStatusCommand command) {
        PurchaseOrder purchaseOrder = orderRepository.findById(command.purchaseOrderId())
                .orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));

        var newStatus = command.newStatus();
        switch (newStatus) {
            case APPROVED:
                purchaseOrder.approve(command.updatedBy());
                break;
            case SENT:
                purchaseOrder.supplierSending();
                break;
            case CANCELLED:
                purchaseOrder.cancel();
                break;
            case REJECTED:
                purchaseOrder.reject();
                break;
            default:
                throw new IllegalArgumentException("Not supported status: " + newStatus);
        }

        orderRepository.save(purchaseOrder);
    }
}