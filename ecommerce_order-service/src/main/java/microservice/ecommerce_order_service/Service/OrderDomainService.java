package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderStatus;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Mapper.OrderItemMapper;
import microservice.ecommerce_order_service.Mapper.OrderMapper;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.OrderItem;
import microservice.ecommerce_order_service.Model.ShippingData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderDomainService {

    private final ClientFacadeService clientFacadeService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;

    @Autowired
    public OrderDomainService(ClientFacadeService clientFacadeService, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        this.clientFacadeService = clientFacadeService;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
    }

    public Order createOrder(OrderInsertDTO orderInsertDTO, OrderMapper orderMapper, OrderItemMapper orderItemMapper) {
        Order order = orderMapper.insertDtoToEntity(orderInsertDTO.getAddressId(), orderInsertDTO.getClientId());
        order.setItems(generateOrderItems(orderInsertDTO.getCartDTO().getCartItems(), order, orderItemMapper));
        return order;
    }

    public void updateOrderPaymentStatus(Order order, boolean isOrderPaid) {
        order.setStatus(isOrderPaid ? OrderStatus.TO_BE_DELIVERED : OrderStatus.PAID_FAILED);
    }

    public String handleDelivery(Order order, boolean isOrderDelivered) {
        int deliveryTries = order.getDeliveryTries() + 1;
        order.setDeliveryTries(deliveryTries);
        order.setLastOrderUpdate(LocalDateTime.now());

        if (!isOrderDelivered) {
            if (deliveryTries > 3) {
                order.setStatus(OrderStatus.CANCELLED);
                return "Order Is Cancelled.";
            }
            return "Order Cannot Be Delivered, We Will Try Again.";
        }
        return "Delivered!";
    }

    public Result<Void> cancelOrder(Order order) {
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return Result.error("Order With Id " + order.getId() + " Can Not Be Canceled");
        }

        order.setLastOrderUpdate(LocalDateTime.now());
        order.setStatus(OrderStatus.CANCELLED);
        return Result.success();
    }

    public List<OrderItem> generateOrderItems(List<CartItemDTO> cartDTOS, Order order, OrderItemMapper orderItemMapper) {
        return cartDTOS.stream()
                .map(cartDTO -> orderItemMapper.cartItemDTOToOrderItem(cartDTO, order))
                .collect(Collectors.toList());
    }

    public CompleteOrderData createCompleteOrderData(Result<ClientDTO> clientResult, Result<AddressDTO> addressResult, Optional<Order> orderOptional, OrderMapper orderMapper) {
        if (!clientResult.isSuccess() || !addressResult.isSuccess() || orderOptional.isEmpty()) {
            throw new RuntimeException("Failed to fetch order data");
        }

        CompleteOrderData completeOrderData = new CompleteOrderData();
        completeOrderData.setClientDTO(clientResult.getData());
        completeOrderData.setAddressDTO(addressResult.getData());
        completeOrderData.setOrderDTO(orderMapper.entityToDTO(orderOptional.get()));

        return completeOrderData;
    }

    public CompletableFuture<OrderDTO> makeOrderDTO(Order order) {
        return clientFacadeService.findClientById(order.getClientId())
                .thenApply(clientResult -> {
                    if (clientResult.isSuccess()) {
                        return createOrderDTO(order, clientResult.getData());
                    } else {
                        throw new RuntimeException("Failed to fetch client data: " + clientResult.getErrorMessage());
                    }
                });
    }

    private OrderDTO createOrderDTO(Order order, ClientDTO clientDTO) {
            OrderDTO orderDTO = orderMapper.entityToDTO(order);

            orderDTO.setClientName(clientDTO.getFirstName() + " " + clientDTO.getLastName());
            orderDTO.setClientPhone(clientDTO.getPhone());
            orderDTO.setItems(order.getItems().stream().map(orderItemMapper::entityToDTO).toList());

            if (order.getShippingData() != null) {
                ShippingData shippingData = order.getShippingData();
                orderDTO.setShippingAddress(shippingData.getAddress() + "," + shippingData.getCity() + "," + shippingData.getState() + "," + shippingData.getCountry() + "," + shippingData.getPostalCode());
                orderDTO.setClientName(shippingData.getRecipientName());
                orderDTO.setClientPhone(shippingData.getPhoneNumber());
            }

            return orderDTO;
    }
}
