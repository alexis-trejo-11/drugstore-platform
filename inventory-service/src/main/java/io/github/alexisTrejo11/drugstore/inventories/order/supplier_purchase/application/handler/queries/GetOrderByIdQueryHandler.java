package io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.handler.queries;

import lombok.RequiredArgsConstructor;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.application.query.GetOrderByIdQuery;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.entity.PurchaseOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.exception.OrderNotFoundException;
import io.github.alexisTrejo11.drugstore.inventories.order.supplier_purchase.domain.port.output.PurchaseOrderRepository;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class GetOrderByIdQueryHandler {
    private final PurchaseOrderRepository orderRepository;

    public PurchaseOrder handle(GetOrderByIdQuery query) {
        return orderRepository.findById(query.purchaseOrderId()).orElseThrow(() -> new OrderNotFoundException("PurchaseOrder not found"));
    }
}
