package microservice.ecommerce_order_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderStatus;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_order_service.Model.Order;
import microservice.ecommerce_order_service.Repository.OrderRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import javax.transaction.Transactional;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class ClientOrderServiceImpl implements ClientOrderService {

    private final OrderRepository orderRepository;
    private final OrderDomainService orderDomainService;
    private final ClientFacadeService clientFacadeServiceFacade;

    public ClientOrderServiceImpl(OrderRepository orderRepository,
                                  OrderDomainService orderDomainService,
                                  ClientFacadeService clientFacadeServiceFacade) {
        this.orderRepository = orderRepository;
        this.orderDomainService = orderDomainService;
        this.clientFacadeServiceFacade = clientFacadeServiceFacade;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Page<OrderDTO>> getCancelledOrdersByClientId(Long clientId, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            Page<Order> orderPage = orderRepository.findByClientIdAndStatusIn(clientId, Arrays.asList(OrderStatus.CANCELLED, OrderStatus.PAID_FAILED) ,pageable);
            List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                    .map(orderDomainService::makeOrderDTO)
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Page<OrderDTO>> getOrdersToBeValidatedByClientId(Long clientId, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            Page<Order> orderPage = orderRepository.findByClientIdAndStatus(clientId, OrderStatus.PENDING_PAYMENT ,pageable);
            List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                    .map(orderDomainService::makeOrderDTO)
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());
            return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
        });
    }


    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Page<OrderDTO>> getCurrentOrdersByClientId(Long clientId, Pageable pageable) {
        return CompletableFuture.supplyAsync(() -> {
            Page<Order> orderPage = orderRepository.findByClientIdAndStatus(clientId, OrderStatus.TO_BE_DELIVERED ,pageable);
            List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                    .map(orderDomainService::makeOrderDTO)
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
            Page<Order> orderPage = orderRepository.findByClientIdAndStatus(clientId, OrderStatus.DELIVERED, pageable);

            List<OrderDTO> orderDTOs = orderPage.getContent().stream()
                    .map(orderDomainService::makeOrderDTO)
                    .map(CompletableFuture::join)
                    .collect(Collectors.toList());

            return new PageImpl<>(orderDTOs, pageable, orderPage.getTotalElements());
        });
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
    public CompletableFuture<Boolean> validateExistingClient(Long clientId) {
        return clientFacadeServiceFacade.findClientById(clientId)
                .thenApply(Result::isSuccess);
    }

}
