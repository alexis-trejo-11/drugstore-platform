package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderStatus;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Adress.AddressFacadeServiceImpl;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Mapper.OrderItemMapper;
import microservice.ecommerce_order_service.Mapper.OrderMapper;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.ShippingData;
import microservice.ecommerce_order_service.Repository.OrderItemRepository;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ClientFacadeService clientFacadeServiceFacade;
    private final OrderDomainService orderDomainService;
    private final ShippingService shippingService;
    private final AddressFacadeServiceImpl addressFacadeServiceImpl;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper,
                            ClientFacadeService clientFacadeServiceFacade,
                            OrderDomainService orderDomainService,
                            ShippingService shippingService,
                            AddressFacadeServiceImpl addressFacadeServiceImpl) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.clientFacadeServiceFacade = clientFacadeServiceFacade;
        this.orderDomainService = orderDomainService;
        this.shippingService = shippingService;
        this.addressFacadeServiceImpl = addressFacadeServiceImpl;
    }


    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Order> createOrderAsync(OrderInsertDTO orderInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Order order = orderDomainService.createOrder(orderInsertDTO, orderMapper, orderItemMapper);
            order = orderRepository.save(order);
            orderItemRepository.saveAll(order.getItems());
            return order;
        });
    }

    @Override
    @Transactional
    public OrderDTO processOrderCreation(Order order, Long clientId, CartDTO cartDTO) {
        order.setClientId(clientId);
        order.setItems(orderDomainService.generateOrderItems(cartDTO.getCartItems(), order, orderItemMapper));
        return orderMapper.entityToDTO(order);
    }

    @Transactional
    @Async("taskExecutor")
    private CompletableFuture<Void> processOrderPaid(ClientDTO clientDTO, Order order, AddressDTO addressDTO) {
        return CompletableFuture.runAsync(() ->  {
            ShippingData shippingData = shippingService.generateShippingData(addressDTO, clientDTO);

            order.setStatus(OrderStatus.TO_BE_DELIVERED);
            order.setLastOrderUpdate(LocalDateTime.now());
            order.setShippingData(shippingData);
            orderRepository.saveAndFlush(order);
        });
    }

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<Void> processOrderNotPaid(Long orderId) {
        return CompletableFuture.runAsync(() ->  {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            if(optionalOrder.isEmpty()) {
                throw new RuntimeException();
            }
            Order order = optionalOrder.get();
            order.setStatus(OrderStatus.PAID_FAILED);
            order.setLastOrderUpdate(LocalDateTime.now());
            orderRepository.saveAndFlush(order);
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> processOrderPayment(CompleteOrderRequest completeOrderRequest, AddressDTO addressDTO, ClientDTO clientDTO, OrderDTO orderDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Order> optionalOrder = orderRepository.findById(completeOrderRequest.getOrderId());
            if (optionalOrder.isEmpty()) {
                return Result.error("Order not found");
            }

            Order order = optionalOrder.get();
            if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
                return Result.error("Order already processed");
            }

            // Create shipping data asynchronously and handle the completion
            return processOrderPaid(clientDTO, order, addressDTO)
                    .thenApply(v -> Result.success())
                    .join();
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Optional<OrderDTO>> getOrderById(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Order> optionalOrder = orderRepository.findById(orderId);
            return optionalOrder.map(orderDomainService::makeOrderDTO).map(CompletableFuture::join);
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<String> deliveryOrder(Long orderId, boolean isOrderDelivered) {
        return CompletableFuture.supplyAsync(() -> {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found"));
            String result = orderDomainService.handleDelivery(order, isOrderDelivered);
            orderRepository.save(order);
            return result;
        });
    }

    @Override
    public CompletableFuture<Result<Void>> validateOrderForDelivery(OrderDTO orderDTO) {
        return CompletableFuture.supplyAsync(() ->
                orderDTO.getStatus() != OrderStatus.PENDING_PAYMENT ? Result.success() : Result.error("Invalid Order"));
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<CompleteOrderData> bringClientDataToCompleteOrder(CompleteOrderRequest completeOrderRequest) {
        CompletableFuture<Result<ClientDTO>> clientFuture = clientFacadeServiceFacade.findClientById(completeOrderRequest.getClientId());
        CompletableFuture<Result<AddressDTO>> addressFuture = addressFacadeServiceImpl.getAddressById(completeOrderRequest.getAddressId());
        CompletableFuture<Optional<Order>> orderFuture = CompletableFuture.supplyAsync(() -> orderRepository.findById(completeOrderRequest.getOrderId()));

        return CompletableFuture.allOf(clientFuture, addressFuture, orderFuture)
                .thenApply(v -> orderDomainService.createCompleteOrderData(clientFuture.join(), addressFuture.join(), orderFuture.join(), orderMapper));
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public void addPaymentIdByOrderId(Long orderId, Long paymentId) {
        orderRepository.findById(orderId)
                .ifPresent(order -> {
                    order.setPaymentId(paymentId);
                    orderRepository.save(order);
                });
    }


}