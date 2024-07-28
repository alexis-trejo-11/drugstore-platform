package at.backend.drugstore.microservice.common_models.ExternalService.Order;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public interface ExternalOrderService {
    Long createOrderAndGetId(OrderInsertDTO orderInsertDTO);
    Result<Void> completeOrder(boolean isOrderPaid, Long orderId, Long addressId, Long clientId);
    void addPaymentIdByOrderId(Long paymentId, Long orderId);
    Optional<OrderDTO> getOrderById(Long orderId);
}
