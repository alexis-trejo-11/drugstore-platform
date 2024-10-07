package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderPaymentStatus;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderStatus;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Address.AddressFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Address.AddressFacadeServiceImpl;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_order_service.Mapper.OrderMapper;
import microservice.ecommerce_order_service.Model.CompleteOrderData;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Model.OrderItem;
import microservice.ecommerce_order_service.Repository.OrderItemRepository;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
@Slf4j
public class OrderServiceImpl implements OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderMapper orderMapper;
    private final ClientFacadeService clientFacadeServiceFacade;
    private final OrderDomainService orderDomainService;
    private final AddressFacadeService addressFacadeServiceImpl;

    public OrderServiceImpl(OrderRepository orderRepository,
                            OrderItemRepository orderItemRepository,
                            OrderMapper orderMapper,
                            @Qualifier("clientFacadeService") ClientFacadeService clientFacadeService,
                            OrderDomainService orderDomainService,
                            AddressFacadeServiceImpl addressFacadeServiceImpl) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderMapper = orderMapper;
        this.clientFacadeServiceFacade = clientFacadeService;
        this.orderDomainService = orderDomainService;
        this.addressFacadeServiceImpl = addressFacadeServiceImpl;
    }


    @Override
    @Transactional
    public OrderDTO createOrder(OrderInsertDTO orderInsertDTO) {
        Order order = orderMapper.insertDtoToEntity(orderInsertDTO.getAddressId(), orderInsertDTO.getClientId());
        List<OrderItem> orderItems = orderDomainService.generateOrderItems(orderInsertDTO.getCartDTO().getCartItems(), order);
        order.setItems(orderItems);

        order = orderRepository.save(order);
        orderItemRepository.saveAll(order.getItems());

        return orderDomainService.createOrderDTO(order);
    }

    @Override
    @Transactional
    public void processOrderNotPaid(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isEmpty()) {
            throw new RuntimeException();
        }
        Order order = optionalOrder.get();
        order.setStatus(OrderStatus.PAID_FAILED);
        order.setLastOrderUpdate(LocalDateTime.now());
        orderRepository.saveAndFlush(order);
    }

    @Override
    @Transactional
    public Result<Void> processOrderPaid(CompleteOrderData completeOrderData) {
        Order order = completeOrderData.getOrder();
        if (order.getStatus() != OrderStatus.PENDING_PAYMENT) {
            return Result.error("Order already processed");
        }

        orderDomainService.processOrderPaid(completeOrderData.getClientDTO(), order, completeOrderData.getAddressDTO());

        return Result.success();
    }

    @Override
    @Transactional
    @Cacheable(value = "orderById", key = "orderId")
    public Optional<OrderDTO> getOrderById(Long orderId) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        return optionalOrder.map(orderDomainService::createOrderDTO);
    }

    @Override
    @Transactional
    public String deliveryOrder(Long orderId, boolean isOrderDelivered) {
        Order order = orderRepository.findById(orderId).orElseThrow(() -> new EntityNotFoundException("Order not found"));

        String result = orderDomainService.handleDelivery(order, isOrderDelivered);
        orderRepository.save(order);
        return result;
    }

    @Override
    public Result<Void> validateOrderForDelivery(OrderDTO orderDTO) {
        return orderDTO.getStatus() != OrderStatus.PENDING_PAYMENT ? Result.success() : Result.error("Invalid Order");
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<CompleteOrderData> bringClientDataToCompleteOrder(OrderPaymentStatus orderPaymentStatus) {
        Optional<Order> optionalOrder =  orderRepository.findById(orderPaymentStatus.getOrderId());
        if (optionalOrder.isEmpty()) {
            throw new RuntimeException("Order Not Found");
        }

        Order order = optionalOrder.get();
        CompletableFuture<ClientDTO> clientFuture = clientFacadeServiceFacade.getClientById(order.getClientId());
        CompletableFuture<Result<AddressDTO>> addressFuture = addressFacadeServiceImpl.getAddressById(order.getAddressId());

        return CompletableFuture.allOf(clientFuture, addressFuture)
                .thenApply(v -> {
                    ClientDTO clientDTO = clientFuture.join();
                    Result<AddressDTO> addressResult = addressFuture.join();

                    if (clientDTO == null || !addressResult.isSuccess()) {
                        throw new RuntimeException("Failed To Fetch Client Data");
                    }

                    return new CompleteOrderData(order, addressResult.getData(),clientDTO);
                });
    }

    @Override
    @Transactional
    public void addPaymentIdByOrderId(Long orderId, Long paymentId) {
        orderRepository.findById(orderId)
                .ifPresent(order -> {
                    order.setPaymentId(paymentId);
                    orderRepository.save(order);
                });
    }
}