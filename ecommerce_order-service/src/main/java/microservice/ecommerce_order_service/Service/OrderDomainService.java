package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderItemDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderStatus;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Mapper.OrderItemMapper;
import microservice.ecommerce_order_service.Mapper.OrderMapper;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.OrderItem;
import microservice.ecommerce_order_service.Model.ShippingData;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderDomainService {

    private final ClientFacadeService clientFacadeService;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final OrderRepository orderRepository;
    private final ShippingService shippingService;

    @Autowired
    public OrderDomainService(ClientFacadeService clientFacadeService,
                              OrderMapper orderMapper,
                              OrderItemMapper orderItemMapper,
                              OrderRepository orderRepository,
                              ShippingService shippingService) {
        this.clientFacadeService = clientFacadeService;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.orderRepository = orderRepository;
        this.shippingService = shippingService;
    }

    @Transactional
    public void processOrderPaid(ClientDTO clientDTO, Order order, AddressDTO addressDTO) {
        ShippingData shippingData = shippingService.generateShippingData(addressDTO, clientDTO);

        order.setStatus(OrderStatus.TO_BE_DELIVERED);
        order.setLastOrderUpdate(LocalDateTime.now());
        order.setShippingData(shippingData);
        orderRepository.saveAndFlush(order);
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

    public List<OrderItem> generateOrderItems(List<CartItemDTO> cartDTOS, Order order) {
        return cartDTOS.stream()
                .map(cartDTO -> orderItemMapper.cartItemDTOToOrderItem(cartDTO, order))
                .collect(Collectors.toList());
    }

    public OrderDTO createOrderDTO(Order order) {
        OrderDTO orderDTO = orderMapper.entityToDTO(order);

        orderDTO.setItems(order.getItems().stream().map(orderItem -> {
             OrderItemDTO orderItemDTO = orderItemMapper.entityToDTO(orderItem);
             orderItemDTO.setCalculateItemTotal();
             return orderItemDTO;
        }).toList());

        if (order.getShippingData() != null) {
            ShippingData shippingData = order.getShippingData();
            orderDTO.setShippingAddress(shippingData.getAddress() + "," + shippingData.getCity() + "," + shippingData.getState() + "," + shippingData.getCountry() + "," + shippingData.getPostalCode());
        }

        return orderDTO;
    }
}
