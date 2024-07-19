package at.backend.drugstore.microservice.common_models.ExternalService.Order;

import at.backend.drugstore.microservice.common_models.DTO.Order.CompleteOrderRequest;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class ExternalOrderServiceImpl implements ExternalOrderService {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(ExternalOrderServiceImpl.class);

    @Value("${ecommerce.order.service.url}")
    private String orderServiceUrl;

    @Autowired
    public ExternalOrderServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public Long createOrderAndGetId(OrderInsertDTO orderInsertDTO) {
        String url = orderServiceUrl + "/v1/api/client-orders/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderInsertDTO> requestEntity = new HttpEntity<>(orderInsertDTO, headers);

        logger.info("Creating order with URL: {}", url);
        try {
            ResponseEntity<ApiResponse<OrderDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<OrderDTO>>() {
                    }
            );

            ApiResponse<OrderDTO> apiResponse = responseEntity.getBody();
            Long orderId = apiResponse.getData().getId();
            logger.info("Order created with ID: {}", orderId);
            return orderId;

        } catch (Exception e) {
            logger.error("Error creating order: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Async
    public Result<Void> completeOrder(boolean isOrderPaid, Long orderId, Long addressId, Long clientId) {
        String url = orderServiceUrl + "/v1/api/orders/complete-order";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        CompleteOrderRequest completeOrderRequest = new CompleteOrderRequest(isOrderPaid, orderId, addressId, clientId);
        HttpEntity<CompleteOrderRequest> requestEntity = new HttpEntity<>(completeOrderRequest, headers);

        logger.info("Completing order with ID: {}", orderId);
        try {
            ResponseEntity<ApiResponse<Void>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<Void>>() {
                    }
            );

            if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND && responseEntity.getBody() != null) {
                logger.warn("Order with ID {} not found.", orderId);
                return Result.error("Order not found.");
            } else {
                logger.info("Order with ID {} completed successfully.", orderId);
                return new Result<>(true, null, responseEntity.getBody().getMessage() + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Error completing order with ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void addPaymentIdByOrderId(Long paymentId, Long orderId) {
        String url = orderServiceUrl + "/v1/api/orders/" + orderId + "/payment/" + paymentId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        logger.info("Adding payment ID {} to order ID {}", paymentId, orderId);
        try {
            ResponseEntity<ApiResponse<Void>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.PUT,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<Void>>() {
                    }
            );

            if (responseEntity.getStatusCode() != HttpStatus.OK) {
                logger.error("Failed to add payment ID {} to order ID {}", paymentId, orderId);
                throw new RuntimeException("Internal Server Error");
            }
        } catch (Exception e) {
            logger.error("Error adding payment ID {} to order ID {}: {}", paymentId, orderId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<OrderDTO> getOrderById(Long orderId) {
        String url = orderServiceUrl + "/v1/api/orders/" + orderId;
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        logger.info("Fetching order with ID: {}", orderId);
        try {
            ResponseEntity<ApiResponse<OrderDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ApiResponse<OrderDTO>>() {
                    }
            );

            if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND && responseEntity.getBody() != null) {
                logger.warn("Order with ID {} not found.", orderId);
                return Optional.empty();
            }

            OrderDTO orderDTO = responseEntity.getBody().getData();
            logger.info("Order with ID {} fetched successfully.", orderId);
            return Optional.of(orderDTO);
        } catch (Exception e) {
            logger.error("Error fetching order with ID {}: {}", orderId, e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
