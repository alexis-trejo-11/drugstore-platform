package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderStatus;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Service
public class ClientOrderServiceImpl implements ClientOrderService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final ClientFacadeService clientFacadeServiceFacade;

    public ClientOrderServiceImpl(OrderRepository orderRepository,
                                  OrderDomainService orderDomainService,
                                  @Qualifier("clientFacadeService") ClientFacadeService clientFacadeService) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.clientFacadeServiceFacade = clientFacadeService;
    }

    @Override
    @Transactional
    public Page<OrderDTO> getCancelledOrdersByClientId(Long clientId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByClientIdAndStatusIn(clientId, Arrays.asList(OrderStatus.CANCELLED, OrderStatus.PAID_FAILED) ,pageable);
        return orderPage.map(orderDomainService::createOrderDTO);
    }

    @Override
    @Transactional
    public Page<OrderDTO> getOrdersToBeValidatedByClientId(Long clientId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByClientIdAndStatus(clientId, OrderStatus.PENDING_PAYMENT ,pageable);
        return orderPage.map(orderDomainService::createOrderDTO);
    }


    @Override
    @Transactional
    public Page<OrderDTO> getCurrentOrdersByClientId(Long clientId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByClientIdAndStatus(clientId, OrderStatus.TO_BE_DELIVERED ,pageable);
         return orderPage.map(orderDomainService::createOrderDTO);
    }

    @Override
    @Transactional
    public Page<OrderDTO> getCompletedOrdersByClientId(Long clientId, Pageable pageable) {
        Page<Order> orderPage = orderRepository.findByClientIdAndStatus(clientId, OrderStatus.DELIVERED, pageable);
        return orderPage.map(orderDomainService::createOrderDTO);
    }

    @Override
    @Transactional
    public Result<Void> cancelOrder(Long orderId) {
            Order order = orderRepository.findById(orderId)
                    .orElseThrow(() -> new EntityNotFoundException("Order not found"));

        return orderDomainService.cancelOrder(order);
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Boolean> validateExistingClient(Long clientId) {
        return clientFacadeServiceFacade.getClientById(clientId)
                .thenApply(Objects::nonNull);
    }
}
