package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface OrderService {
    OrderDTO createOrder(OrderInsertDTO orderInsertDTO);
    Result<Void> createShippingData(ClientDTO clientDTO, OrderDTO orderDTO, AddressDTO addressDTO);
    Result<Void> validateOrderPayment(boolean isOrderPaid, Long orderId);
    OrderDTO getOrderById (Long orderId);
    Boolean validateExistingClient(Long clientId);
    Page<OrderDTO> getOrdersByClientId(Long clientId, Pageable pageable);
    String deliveryOrder(Long orderId, boolean isOrderDelivered);
    Result<Void> cancelOrder(Long orderId);
    Result<Void> validateOrderForDelivery(OrderDTO orderDTO);


    }
