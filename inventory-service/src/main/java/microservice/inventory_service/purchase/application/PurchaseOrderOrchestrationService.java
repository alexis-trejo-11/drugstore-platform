package microservice.inventory_service.purchase.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.inventory.domain.entity.valueobject.id.UserId;
import microservice.inventory_service.purchase.application.command.CreatePurchaseOrderCommand;
import microservice.inventory_service.purchase.application.command.ReceivePurchaseOrderCommand;
import microservice.inventory_service.purchase.application.command.UpdatePurchaseOrderStatusCommand;
import microservice.inventory_service.purchase.application.handler.ReceivePurchaseOrderCommandHandler;
import microservice.inventory_service.purchase.application.handler.CreatePurchaseOrderCommandHandler;
import microservice.inventory_service.purchase.application.handler.UpdatePurchaseOrderStatusCommandHandler;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderId;
import microservice.inventory_service.purchase.domain.entity.PurchaseOrderStatus;
import microservice.inventory_service.purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class PurchaseOrderOrchestrationService {

    private final CreatePurchaseOrderCommandHandler createPOHandler;
    private final UpdatePurchaseOrderStatusCommandHandler updatePOStatusHandler;
    private final ReceivePurchaseOrderCommandHandler receivePOHandler;
    private final PurchaseOrderRepository purchaseOrderRepository;

    @Transactional
    public PurchaseOrderId createAndApprovePurchaseOrder(CreatePurchaseOrderCommand command, UserId approvedBy) {
        PurchaseOrderId orderId = createPOHandler.handle(command);

        var approveCommand = new UpdatePurchaseOrderStatusCommand(orderId, PurchaseOrderStatus.APPROVED, approvedBy);
        updatePOStatusHandler.handle(approveCommand);

        return orderId;
    }

    @Transactional
    public void receiveAndProcessPurchaseOrder(ReceivePurchaseOrderCommand command) {
        receivePOHandler.handle(command);
    }

    @Transactional
    public void sendPurchaseOrderToSupplier(PurchaseOrderId purchaseOrderId) {
        PurchaseOrder purchaseOrder = purchaseOrderRepository.findById(purchaseOrderId)
                .orElseThrow(() -> new IllegalStateException("Purchase order not found"));

        purchaseOrder.sendToSupplier();
        purchaseOrderRepository.save(purchaseOrder);
    }
}