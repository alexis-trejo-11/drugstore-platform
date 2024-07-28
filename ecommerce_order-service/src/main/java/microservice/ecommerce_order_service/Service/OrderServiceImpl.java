package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Cart.CartItemDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.*;
import at.backend.drugstore.microservice.common_models.ExternalService.Adress.ExternalAddressService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_order_service.Mapper.OrderItemMapper;
import microservice.ecommerce_order_service.Mapper.OrderMapper;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.OrderItem;
import microservice.ecommerce_order_service.Model.ShippingData;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ExternalClientService externalClientService;
    private final ExternalAddressService externalAddressService;

    @Autowired
    public OrderServiceImpl(OrderRepository orderRepository,
                            ExternalClientService externalClientService,
                            OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper,
                            ExternalAddressService externalAddressService) {
        this.orderRepository = orderRepository;
        this.externalClientService = externalClientService;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.externalAddressService = externalAddressService;
    }


    @Override
    @Async
    @Transactional
    public Order createOrder(OrderInsertDTO orderInsertDTO) {
           Order order = orderMapper.insertDtoToEntity(orderInsertDTO.getAddressId(), orderInsertDTO.getClientId());
           List<OrderItem> orderItems = generateOrderItems(orderInsertDTO.getCartDTO().getCartItems(), order);
           order.setItems(orderItems);

           orderRepository.saveAndFlush(order);

           return order;
    }

    public OrderDTO processOrderCreation(Order order, Long clientId, CartDTO cartDTO) {
        order.setClientId(clientId);
        order.setItems(generateOrderItems(cartDTO.getCartItems(),order));
        return orderMapper.entityToDTO(order);
    }

    @Async
    @Transactional
    public void createShippingData(ClientDTO clientDTO, OrderDTO orderDTO, AddressDTO addressDTO) {
            Optional<Order> optionalOrder = orderRepository.findById(orderDTO.getId());
            Order order = optionalOrder.get();

            ShippingData shippingData = generateShippingData(addressDTO, clientDTO);
            order.setShippingData(shippingData);
            orderRepository.saveAndFlush(order);
    }

    @Override
    @Async
    @Transactional
    public void processOrderPayment(CompleteOrderRequest completeOrderRequest, AddressDTO addressDTO, ClientDTO clientDTO, OrderDTO orderDTO) {
        updateOrderPaymentStatus(completeOrderRequest.getOrderId(), completeOrderRequest.isOrderPaid());
        if (completeOrderRequest.isOrderPaid()) {
            createShippingData(clientDTO, orderDTO, addressDTO);
        }
    }

    private void updateOrderPaymentStatus(Long orderId, boolean isoOrderPaid) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new EntityNotFoundException("Order Not Found");
        }

        Order order = optionalOrder.get();

        if (!isoOrderPaid) {
            order.setStatus(OrderStatus.PAID_FAILED);
            orderRepository.saveAndFlush(order);
        } else {
            order.setStatus(OrderStatus.PAID);
            orderRepository.saveAndFlush(order);
        }
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

    @Async
    @Transactional
    public CompleteOrderData bringClientDataToCompleteOrder(CompleteOrderRequest completeOrderRequest) {
        Long addressId = completeOrderRequest.getAddressId();
        Long clientId = completeOrderRequest.getClientId();
        Long orderId = completeOrderRequest.getOrderId();

        CompleteOrderData completeOrderData = new CompleteOrderData();

        Result<ClientDTO> clientDTOResult = externalClientService.findClientById(clientId);
        completeOrderData.setClientDTO(clientDTOResult.getData());

        Result<AddressDTO> addressResult = externalAddressService.getAddressId(addressId);
        completeOrderData.setAddressDTO(addressResult.getData());

        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        OrderDTO orderDTO = orderMapper.entityToDTO(optionalOrder.get());
        completeOrderData.setOrderDTO(orderDTO);

        return completeOrderData;
    }

    @Transactional
    @Override
    public void addPaymentIdByOrderId(Long orderId, Long paymentId) {
        Optional<Order> orderOptional = orderRepository.findById(orderId);
        orderOptional.get().setPaymentId(paymentId);
        orderRepository.saveAndFlush(orderOptional.get());
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
        return orderOptional.orElse(null);
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

        Result<ClientDTO> clientDTOResult = externalClientService.findClientById(order.getClientId());
        ClientDTO clientDTO = clientDTOResult.getData();

        orderDTO.setClientName(clientDTO.getFirstName() + " " + clientDTO.getLastName());
        orderDTO.setClientPhone(clientDTO.getPhone());
        orderDTO.setItems(order.getItems().stream().map(orderItemMapper::entityToDTO).toList());

        if (order.getShippingData() != null) {
            ShippingData shippingData = order.getShippingData();
            orderDTO.setShippingAddress(shippingData.getAddress() + "," + shippingData.getCity() +  "," + shippingData.getState() + "," + shippingData.getCountry() + "," + shippingData.getPostalCode());
            orderDTO.setClientName(shippingData.getRecipientName());
            orderDTO.setClientPhone(shippingData.getPhoneNumber());
        }

        return orderDTO;
    }

    private List<OrderItem> generateOrderItems(List<CartItemDTO> cartDTOS, Order order) {
        return cartDTOS.stream()
                .map(cartDTO -> orderItemMapper.cartItemDTOToOrderItem(cartDTO, order))
                .collect(Collectors.toList());
    }
}

