package microservice.ecommerce_order_service.Repository;


import at.backend.drugstore.microservice.common_models.DTO.Order.OrderStatus;
import microservice.ecommerce_order_service.Model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByClientIdAndStatus(Long clientId, OrderStatus status, Pageable pageable);
    Page<Order> findByClientIdAndStatusIn(Long clientId, List<OrderStatus> statuses, Pageable pageable);

}