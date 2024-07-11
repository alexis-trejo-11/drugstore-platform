package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.*;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_order_service.Mapper.OrderItemMapper;
import microservice.ecommerce_order_service.Mapper.OrderMapper;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.OrderItem;
import microservice.ecommerce_order_service.Model.ShippingData;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ExternalAddressService externalAddressService;
    private final ExternalClientService externalClientService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository, ExternalClientService externalClientService, OrderMapper orderMapper, OrderItemMapper orderItemMapper, ExternalAddressService externalAddressService) {
        this.orderRepository = orderRepository;
        this.externalClientService = externalClientService;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.externalAddressService = externalAddressService;
    }


    @Override
    @Async
    @Transactional
    public OrderDTO createOrder(OrderInsertDTO orderInsertDTO) {
           Order order = orderMapper.insertDtoToEntity(orderInsertDTO);
           List<OrderItem> orderItems = generateOrderItems(orderInsertDTO.getItems(), order);
           order.setItems(orderItems);

           orderRepository.saveAndFlush(order);

           OrderDTO orderDTO = orderMapper.entityToDTO(order);
           return orderDTO;
    }

    @Override
    @Async
    @Transactional
    public Result<Void> createShippingData(ClientDTO clientDTO, OrderDTO orderDTO, AddressDTO addressDTO) {
            if (orderDTO.getStatus() != OrderStatus.PAID) {
                return new Result<>(false, null, "Order Not Paid!.");
            }

            Optional<Order> optionalOrder = orderRepository.findById(orderDTO.getId());
            if (optionalOrder.isEmpty()) {
                return new Result<>(false, null, "Order Not Found");
            }
            Order order = optionalOrder.get();

            ShippingData shippingData = generateShippingData(addressDTO, clientDTO);
            order.setShippingData(shippingData);
            orderRepository.saveAndFlush(order);

            return Result.success();
    }

    @Override
    @Async
    @Transactional
    public Result<Void> validateOrderPayment(boolean isOrderPaid, Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            return Result.error("Order With Id " + orderId + " Not Found");
        }

        Order order = optionalOrder.get();

        if (!isOrderPaid) {
            order.setStatus(OrderStatus.PAID_FAILED);
            orderRepository.saveAndFlush(order);
            return Result.success();
        }

        order.setStatus(OrderStatus.PAID);
        orderRepository.saveAndFlush(order);
        return Result.success();
    }



    @Override
    @Async
    @Transactional
    public OrderDTO getOrderById (Long orderId)  {
    Optional<Order> orderOptional = orderRepository.findById(orderId);
        return orderOptional.map(this::makeOrderDTO).orElse(null);
    }

    @Override
    @Async
    @Transactional
    public Boolean validateExistingClient(Long clientId)  {
        Result<ClientDTO> clientDTOResult = externalClientService.findClientById(clientId);

        return clientDTOResult.isSuccess();
    }

    @Override
    @Async
    @Transactional
    public Page<OrderDTO> getOrdersByClientId(Long clientId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByClientId(clientId, pageable);
        return orderPage.map(this::makeOrderDTO);
    }


    @Override
    @Async
    @Transactional
    public String deliveryOrder(Long orderId, boolean isOrderDelivered) {
        Order order = findOrderById(orderId);
        assert order != null;

         return handleDelivery(order, isOrderDelivered);
    }

    @Override
    @Async
    public Result<Void> validateOrderForDelivery(OrderDTO orderDTO) {
        if (orderDTO.getStatus() != OrderStatus.PAID) {
            return Result.error("Invalid Order");
        }
        return Result.success();
    }

    @Override
    @Async
    @Transactional
    public Result<Void> cancelOrder(Long orderId) {
            Optional<Order> orderOptional = orderRepository.findById(orderId);
            Order order = orderOptional.get();

            if (order.getStatus() != OrderStatus.PENDING) {
                return Result.error("Order With Id " + orderId + " Can Not Be Canceled");
            }

            order.setLastOrderUpdate(LocalDateTime.now());
            order.setStatus(OrderStatus.CANCELLED);
            orderRepository.saveAndFlush(order);
            return Result.success();
    }

    private String handleDelivery(Order order, boolean isOrderDelivered) {
        int deliveryTries = order.getDeliveryTries() + 1;

        if (!isOrderDelivered) {
            order.setDeliveryTries(deliveryTries);
            order.setLastOrderUpdate(LocalDateTime.now());

            if (deliveryTries > 3) {
                order.setStatus(OrderStatus.CANCELLED);
                orderRepository.saveAndFlush(order);
                return "Order Is Cancelled.";
            } else {
                orderRepository.saveAndFlush(order);
                return "Order Cannot Be Delivered, We Will Try Again.";
            }
        }
        return "Delivered!";
    }

    private Order findOrderById(Long orderId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        if (orderOptional.isEmpty()) {
            return null;
        }
        return orderOptional.get();
    }

    private ShippingData generateShippingData(AddressDTO addressDTO, ClientDTO clientDTO) {
        ShippingData shippingData = new ShippingData();
        StringBuilder address = new StringBuilder();
        address.append(addressDTO.getStreet());
        address.append(" #").append(addressDTO.getHouseNumber());

        if (addressDTO.getInnerNumber() != null) {
            address.append(" (interior #").append(addressDTO.getInnerNumber()).append(")");
        }

        shippingData.setAddress(address.toString());
        shippingData.setCity(addressDTO.getCity());
        shippingData.setState(addressDTO.getState());
        shippingData.setCountry(addressDTO.getCountry());
        shippingData.setPostalCode(String.valueOf(addressDTO.getZipCode()));
        shippingData.setPhoneNumber(clientDTO.getPhone());
        shippingData.setRecipientName(clientDTO.getFirstName() + " " + clientDTO.getLastName());
        shippingData.setShippingCost(BigDecimal.ZERO);
        shippingData.setTrackingNumber(GenerateTrackingNumber());

        return shippingData;
    }

    private String GenerateTrackingNumber() {
        int[] numbers = {1, 2, 3, 4, 5, 6, 7, 8, 9, 0};
        StringBuilder trackingNumber = new StringBuilder();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            int randomIndex = random.nextInt(numbers.length);
            int randomNumber = numbers[randomIndex];
            trackingNumber.append(randomNumber);
        }

        return trackingNumber.toString();
    }

    private OrderDTO makeOrderDTO(Order order){
        OrderDTO orderDTO = orderMapper.entityToDTO(order);

        if (order.getShippingData() != null) {
            ShippingData shippingData = order.getShippingData();
            orderDTO.setShippingAddress(shippingData.getAddress());
            orderDTO.setClientName(shippingData.getRecipientName());
            orderDTO.setClientPhone(shippingData.getPhoneNumber());
        }

        orderDTO.setItems(order.getItems().stream().map(orderItemMapper::entityToDTO).toList());

        return orderDTO;
    }

    private List<OrderItem> generateOrderItems(List<OrderItemInsertDTO> orderItemInsertDTOS, Order order) {
        List<OrderItem> orderItems = new ArrayList<>();
        for (var orderItemInsertDTO : orderItemInsertDTOS) {
            OrderItem orderItem = new OrderItem();
            orderItem.setProductId(orderItemInsertDTO.getProductId());
            orderItem.setOrder(order);
            orderItem.setQuantity(orderItemInsertDTO.getQuantity());

            orderItems.add(orderItem);
        }

        return orderItems;
    }
}

