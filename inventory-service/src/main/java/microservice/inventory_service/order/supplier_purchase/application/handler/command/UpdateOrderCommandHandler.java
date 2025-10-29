package microservice.inventory_service.order.supplier_purchase.application.handler.command;

import lombok.RequiredArgsConstructor;
import microservice.inventory_service.order.supplier_purchase.application.command.InsertOrderCommand;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrder;
import microservice.inventory_service.order.supplier_purchase.domain.entity.PurchaseOrderItem;
import microservice.inventory_service.order.supplier_purchase.domain.exception.OrderNotFoundException;
import microservice.inventory_service.order.supplier_purchase.domain.port.output.OrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateOrderCommandHandler {
    private final OrderRepository orderRepository;

    @Transactional
    public void handle(InsertOrderCommand command) {
        if (!orderRepository.existsById(command.purchaseOrderId())) {
            throw new OrderNotFoundException("PurchaseOrder with ID " + command.purchaseOrderId() + " does not exist.");
        }

        List<PurchaseOrderItem> items = command.items().stream()
                .map(itemCommand -> PurchaseOrderItem.create(
                        itemCommand.id(),
                        itemCommand.productId(),
                        itemCommand.productName(),
                        itemCommand.quantity()
                ))
                .toList();

        PurchaseOrder purchaseOrder = PurchaseOrder.create(
                command.purchaseOrderId(),
                command.supplierId(),
                command.supplierName(),
                items,
                command.expectedDeliveryDate(),
                command.deliveryLocation(),
                command.createdBy()
        );

        orderRepository.save(purchaseOrder);
    }

}