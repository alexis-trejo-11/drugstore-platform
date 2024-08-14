package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Order;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
public class OrderFacadeServiceImpl implements OrderFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> orderServiceUrlProvider;

    public OrderFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> orderServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.orderServiceUrlProvider = orderServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Long> createOrderAndGetId(OrderInsertDTO orderInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            String url = orderServiceUrlProvider.get() + "/v1/api/orders/create";
            log.info("Creating order with URL: {}", url);
            log.info("Request Payload: {}", orderInsertDTO);

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
                HttpEntity<OrderInsertDTO> requestEntity = new HttpEntity<>(orderInsertDTO, headers);

                ResponseEntity<ResponseWrapper<OrderDTO>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<ResponseWrapper<OrderDTO>>() {}
                );

                ResponseWrapper<OrderDTO> response = responseEntity.getBody();

                if (response != null && response.getData() != null) {
                    Long orderId = response.getData().getId();
                    log.info("Order created with ID: {}", orderId);
                    return orderId;
                } else {
                    log.error("Failed to create order.");
                    return null;
                }
            } catch (Exception e) {
                log.error("Error creating order: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> completeOrder(boolean isOrderPaid, Long orderId, Long addressId, Long clientId) {
        return CompletableFuture.runAsync(() -> {
            String url = orderServiceUrlProvider.get() + "/v1/api/orders/complete-order";
            CompleteOrderRequest completeOrderRequest = new CompleteOrderRequest(isOrderPaid, orderId, addressId, clientId);

            log.info("Completing order with ID: {}", orderId);

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
                HttpEntity<CompleteOrderRequest> requestEntity = new HttpEntity<>(completeOrderRequest, headers);

                restTemplate.exchange(
                        url,
                        HttpMethod.PUT,
                        requestEntity,
                        Void.class
                );
            } catch (Exception e) {
                log.error("Error completing order with ID {}: {}", orderId, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> addPaymentIdByOrderId(Long paymentId, Long orderId) {
        return CompletableFuture.runAsync(() -> {
            String url = orderServiceUrlProvider.get() + "/v1/api/orders/" + orderId + "/payment/" + paymentId;

            log.info("Adding payment ID {} to order ID {}", paymentId, orderId);

            try {
                restTemplate.exchange(
                        url,
                        HttpMethod.PUT,
                        null,
                        Void.class
                );
                log.info("Successfully added payment ID {} to order ID {}", paymentId, orderId);
            } catch (Exception e) {
                log.error("Error adding payment ID {} to order ID {}: {}", paymentId, orderId, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<OrderDTO> getOrderById(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = orderServiceUrlProvider.get() + "/v1/api/orders/" + orderId;

            log.info("Fetching order with ID: {}", orderId);

            try {
                ResponseEntity<ResponseWrapper<OrderDTO>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseWrapper<OrderDTO>>() {}
                );

                ResponseWrapper<OrderDTO> response = responseEntity.getBody();

                if (response != null && response.getData() != null) {
                    log.info("Order with ID {} fetched successfully.", orderId);
                    return response.getData();
                } else {
                    log.error("Failed to fetch order with ID {}.", orderId);
                    return null;
                }
            } catch (Exception e) {
                log.error("Error fetching order with ID {}: {}", orderId, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }
}
