package microservice.inventory_service.external.order.application;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.internal.core.inventory.domain.entity.valueobject.UserId;
import microservice.inventory_service.external.order.application.command.CreatePurchaseOrderCommand;
import microservice.inventory_service.external.order.application.command.ReceivePurchaseOrderCommand;
import microservice.inventory_service.external.order.application.command.UpdatePurchaseOrderStatusCommand;
import microservice.inventory_service.external.order.application.handler.command.ReceivePurchaseOrderCommandHandler;
import microservice.inventory_service.external.order.application.handler.command.CreatePurchaseOrderCommandHandler;
import microservice.inventory_service.external.order.application.handler.command.UpdatePurchaseOrderStatusCommandHandler;
import microservice.inventory_service.external.order.domain.entity.PurchaseOrder;
import microservice.inventory_service.external.order.domain.entity.valueobject.PurchaseOrderId;
import microservice.inventory_service.external.order.domain.entity.valueobject.PurchaseOrderStatus;
import microservice.inventory_service.external.order.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class OrderCommandServiceImpl {
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