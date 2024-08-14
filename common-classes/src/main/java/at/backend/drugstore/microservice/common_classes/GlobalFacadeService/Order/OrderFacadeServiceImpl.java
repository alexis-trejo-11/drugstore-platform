package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Order;

import at.backend.drugstore.microservice.common_classes.DTOs.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class OrderFacadeServiceImpl implements OrderFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(OrderFacadeServiceImpl.class);
    private final RestTemplate restTemplate;

    private final String orderServiceUrl = "http://ecommerce_order-service:8088";

    public OrderFacadeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Long> createOrderAndGetId(OrderInsertDTO orderInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            String url = orderServiceUrl + "/v1/api/orders/create";
            logger.info("Creating order with URL: {}", url);
            logger.info("Request Payload: {}", orderInsertDTO);

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
                    logger.info("Order created with ID: {}", orderId);
                    return orderId;
                } else {
                    logger.error("Failed to create order.");
                    return null;
                }
            } catch (Exception e) {
                logger.error("Error creating order: {}", e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> completeOrder(boolean isOrderPaid, Long orderId, Long addressId, Long clientId) {
        return CompletableFuture.runAsync(() -> {
            String url = orderServiceUrl + "/v1/api/orders/complete-order";
            CompleteOrderRequest completeOrderRequest = new CompleteOrderRequest(isOrderPaid, orderId, addressId, clientId);

            logger.info("Completing order with ID: {}", orderId);

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
                logger.error("Error completing order with ID {}: {}", orderId, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Void> addPaymentIdByOrderId(Long paymentId, Long orderId) {
        return CompletableFuture.runAsync(() -> {
            String url = orderServiceUrl + "/v1/api/orders/" + orderId + "/payment/" + paymentId;

            logger.info("Adding payment ID {} to order ID {}", paymentId, orderId);

            try {
                restTemplate.exchange(
                        url,
                        HttpMethod.PUT,
                        null,
                        Void.class
                );
                logger.info("Successfully added payment ID {} to order ID {}", paymentId, orderId);
            } catch (Exception e) {
                logger.error("Error adding payment ID {} to order ID {}: {}", paymentId, orderId, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<OrderDTO> getOrderById(Long orderId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = orderServiceUrl + "/v1/api/orders/" + orderId;

            logger.info("Fetching order with ID: {}", orderId);

            try {
                ResponseEntity<ResponseWrapper<OrderDTO>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseWrapper<OrderDTO>>() {}
                );

                ResponseWrapper<OrderDTO> response = responseEntity.getBody();

                if (response != null && response.getData() != null) {
                    logger.info("Order with ID {} fetched successfully.", orderId);
                    return response.getData();
                } else {
                    logger.error("Failed to fetch order with ID {}.", orderId);
                    return null;
                }
            } catch (Exception e) {
                logger.error("Error fetching order with ID {}: {}", orderId, e.getMessage(), e);
                throw new RuntimeException(e);
            }
        });
    }
}
