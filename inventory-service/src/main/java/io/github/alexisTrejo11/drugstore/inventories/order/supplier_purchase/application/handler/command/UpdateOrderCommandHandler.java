package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.command;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.command.InitOrderCommand;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.PurchaseOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.PurchaseOrderItem;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.valueobject.CreatePurchaseOrderParams;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.exception.OrderNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
@RequiredArgsConstructor
public class UpdateOrderCommandHandler {
    private final PurchaseOrderRepository orderRepository;

    @Transactional
    public void handle(InitOrderCommand command) {
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
                CreatePurchaseOrderParams.builder()
                        .supplierId(command.supplierId())
                        .supplierName(command.supplierName())
                        .id(command.purchaseOrderId())
                        .items(items)
                        .expectedDeliveryDate(command.expectedDeliveryDate())
                        .deliveryLocation(command.deliveryLocation())
                        .createdBy(command.createdBy()
                        ).build()
        );

        orderRepository.save(purchaseOrder);
    }

}