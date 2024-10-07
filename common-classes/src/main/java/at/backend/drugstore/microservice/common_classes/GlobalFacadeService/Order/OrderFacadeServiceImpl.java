package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Order;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderPaymentStatus;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
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
            String url = orderServiceUrlProvider.get() + "/v1/drugstore/ecommerce-orders/create";
            log.info("Creating order with URL: {}", url);
            log.info("Request Payload: {}", orderInsertDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<OrderInsertDTO> requestEntity = new HttpEntity<>(orderInsertDTO, headers);

            ResponseEntity<ResponseWrapper<OrderDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseWrapper<OrderDTO>>() {}
            );
            ResponseWrapper<OrderDTO> responseEntityBody = responseEntity.getBody();
            assert responseEntityBody != null;

            if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
                 throw new RuntimeException(responseEntity.getBody().getMessage());
            }

            Long orderId = responseEntityBody.getData().getId();
            log.info("Order created with ID: {}", orderId);
            return orderId;
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> completeOrder(boolean isOrderPaid, Long orderId, Long addressId, Long clientId) {
        return CompletableFuture.runAsync(() -> {
            String url = orderServiceUrlProvider.get() + "/v1/drugstore/ecommerce-orders/complete-order";
            OrderPaymentStatus orderPaymentStatus = new OrderPaymentStatus(isOrderPaid, orderId);

            log.info("Completing order with ID: {}", orderId);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<OrderPaymentStatus> requestEntity = new HttpEntity<>(orderPaymentStatus, headers);

            restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    Void.class
            );
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> addPaymentIdByOrderId(Long paymentId, Long orderId) {
        return CompletableFuture.runAsync(() -> {
            String url = orderServiceUrlProvider.get() + "/v1/drugstore/ecommerce-orders/" + orderId + "/payment/" + paymentId;

            log.info("Adding payment ID {} to order ID {}", paymentId, orderId);

            restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    null,
                    Void.class
            );
            log.info("Successfully added payment ID {} to order ID {}", paymentId, orderId);
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<OrderDTO> getOrderById(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = orderServiceUrlProvider.get() + "/v1/drugstore/ecommerce-orders/" + orderId;

            log.info("Fetching order with ID: {}", orderId);

            ResponseEntity<ResponseWrapper<OrderDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ResponseWrapper<OrderDTO>>() {}
            );
            ResponseWrapper<OrderDTO> responseEntityBody = responseEntity.getBody();
            assert responseEntityBody != null;

            if (responseEntityBody.getStatusCode() == HttpStatus.NOT_FOUND.value()) {
                return null;
            }

            return responseEntityBody.getData();
        });
    }
}
