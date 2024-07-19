package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderService {
    Order createOrder(OrderInsertDTO orderInsertDTO);
    OrderDTO getOrderById (Long orderId);
    Boolean validateExistingClient(Long clientId);
    Page<OrderDTO> getOrdersByClientId(Long clientId, Pageable pageable);
    String deliveryOrder(Long orderId, boolean isOrderDelivered);
    Result<Void> cancelOrder(Long orderId);
    Result<Void> validateOrderForDelivery(OrderDTO orderDTO);
    void processOrderPayment(CompleteOrderRequest completeOrderRequest, AddressDTO addressDTO, ClientDTO clientDTO, OrderDTO orderDTO);
    OrderDTO processOrderCreation(Order order, Long clientId, CartDTO cartDTO);
    CompleteOrderData bringClientDataToCompleteOrder(CompleteOrderRequest completeOrderRequest);
    void addPaymentIdByOrderId(Long orderId, Long paymentId);
    }
