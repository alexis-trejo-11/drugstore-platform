package io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.port;

import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SaleOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.valueobject.SaleOrderId;
import io.github.alexisTrejo11.drugstore.inventories.shared.domain.order.OrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface SaleOrderRepository {
    Optional<SaleOrder> findById(SaleOrderId saleOrderId);
    Page<SaleOrder> findByCustomerId(String userId, Pageable pageable);
    Page<SaleOrder> findByStatus(OrderStatus status, Pageable pageable);

    SaleOrder save(SaleOrder saleOrder);
    boolean existsById(SaleOrderId saleOrderId);
    void deleteById(SaleOrderId saleOrderId);

}
