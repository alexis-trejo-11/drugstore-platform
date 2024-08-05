package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Cart.CartDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderStatus;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Mapper.OrderItemMapper;
import microservice.ecommerce_order_service.Mapper.OrderMapper;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.ShippingData;
import microservice.ecommerce_order_service.Repository.OrderItemRepository;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final ExternalServiceFacade externalServiceFacade;
    private final OrderDomainService orderDomainService;
    private final ShippingService shippingService;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            OrderMapper orderMapper,
                            OrderItemMapper orderItemMapper,
                            ExternalServiceFacade externalServiceFacade,
                            OrderDomainService orderDomainService,
                            ShippingService shippingService) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.orderItemMapper = orderItemMapper;
        this.externalServiceFacade = externalServiceFacade;
        this.orderDomainService = orderDomainService;
        this.shippingService = shippingService;
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

            order.setStatus(OrderStatus.PAID);
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
            if (order.getStatus() != OrderStatus.PENDING) {
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
            return optionalOrder.map(this::makeOrderDTO).map(CompletableFuture::join);
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> validateExistingClient(Long clientId) {
        return externalServiceFacade.getClientById(clientId)
                .thenApply(Result::isSuccess);
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Page<OrderDTO>> getCurrentOrdersByClientId(Long clientId, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            Page<Order> orderPage = orderRepository.findByClientIdAndStatus(clientId, OrderStatus.PENDING ,pageable);
            List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                    .map(this::makeOrderDTO)
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
        });
    }


    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Page<OrderDTO>> getCompletedOrdersByClientId(Long clientId, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
             Page<Order> orderPage = orderRepository.findByClientIdAndStatusIn(clientId, Arrays.asList(OrderStatus.PAID, OrderStatus.PAID_FAILED), pageable);

             List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                    .map(this::makeOrderDTO)
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
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
                orderDTO.getStatus() == OrderStatus.PAID ? Result.success() : Result.error("Invalid Order"));
    }

    @Override
    @Transactional
    public CompletableFuture<Result<Void>> cancelOrder(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found"));

            return orderDomainService.cancelOrder(order);
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<CompleteOrderData> bringClientDataToCompleteOrder(CompleteOrderRequest completeOrderRequest) {
        CompletableFuture<Result<ClientDTO>> clientFuture = externalServiceFacade.getClientById(completeOrderRequest.getClientId());
        CompletableFuture<Result<AddressDTO>> addressFuture = externalServiceFacade.getAddressById(completeOrderRequest.getAddressId());
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

    private CompletableFuture<OrderDTO> makeOrderDTO(Order order) {
        return externalServiceFacade.getClientById(order.getClientId())
                .thenApply(clientResult -> {
                    if (clientResult.isSuccess()) {
                        return orderDomainService.createOrderDTO(order, clientResult.getData(), orderMapper, orderItemMapper);
                    } else {
                        throw new RuntimeException("Failed to fetch client data: " + clientResult.getErrorMessage());
                    }
                });
    }
}