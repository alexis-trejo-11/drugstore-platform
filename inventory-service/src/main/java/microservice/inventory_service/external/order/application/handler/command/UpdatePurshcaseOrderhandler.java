package microservice.inventory_service.external.order.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.external.order.application.command.UpdatePurchaseOrderStatusCommand;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.exception.PurchaseOrderNotFoundException;
import microservice.inventory_service.external.order.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class UpdatePurshcaseOrderhandler {
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public void handle(UpdatePurchaseOrderStatusCommand command) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(command.orderId())
                .orElseThrow(() -> new PurchaseOrderNotFoundException("Purchase order not found"));

        purchaseOrderRepository.save(purchaseOrder);
    }
}
