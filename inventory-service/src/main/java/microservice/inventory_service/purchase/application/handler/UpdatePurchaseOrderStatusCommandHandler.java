package microservice.inventory_service.purchase.application.handler;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.purchase.application.command.UpdatePurchaseOrderStatusCommand;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.purchase.domain.exception.PurchaseOrderNotFoundException;
import microservice.inventory_service.purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdatePurchaseOrderStatusCommandHandler {
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public void handle(UpdatePurchaseOrderStatusCommand command) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(command.orderId())
                .orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase order not found"));

        var newStatus = command.newStatus();
        switch (newStatus) {
            case APPROVED:
                purchaseOrder.approve(command.updatedBy());
                break;
            case SENT:
                purchaseOrder.sendToSupplier();
                break;
            case CANCELLED:
                purchaseOrder.cancel();
                break;
            case REJECTED:
                purchaseOrder.reject();
                break;
            default:
                throw new IllegalArgumentException("Invalid status update");
        }

        purchaseOrderRepository.save(purchaseOrder);
    }
}