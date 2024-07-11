package at.backend.drugstore.microservice.common_models.ExternalService.Order;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderDTO;
import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.web.client.RestTemplate;

public class ExternalOrderServiceImpl implements ExternalOrderService {

    private final RestTemplate restTemplate;

    @Value("${ecommerce.order.service.url}")
    private String orderServiceUrl;

    @Autowired
    public ExternalOrderServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public Result<Void> createOrder(OrderInsertDTO orderInsertDTO) {
        String url = orderServiceUrl + "/orders/create";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<OrderInsertDTO> requestEntity = new HttpEntity<>(orderInsertDTO, headers);

        try {
            ResponseEntity<ResponseWrapper<OrderDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseWrapper<OrderDTO>>() {}
            );

            if (responseEntity.getStatusCode() == HttpStatus.OK && responseEntity.getBody() != null) {
                ResponseWrapper<OrderDTO> responseWrapper = responseEntity.getBody();
                if (responseWrapper.getData() != null) {
                    return Result.success(null);
                } else {
                    return new Result<>(false, null, responseWrapper.getMessage());
                }
            } else {
                return new Result<>(false, null, "Failed to create order, status code: " + responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Exception occurred while creating order: " + e.getMessage());
        }
    }
}
