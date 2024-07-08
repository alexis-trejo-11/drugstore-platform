package at.backend.drugstore.microservice.common_models.ExternalService.Cart;

import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalCartServiceImpl implements ExternalCartService {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(ExternalCartServiceImpl.class);

    @Value("${ecommerce.cart.service.url}")
    private String ecommerceCartServiceUrl;

    @Autowired
    public ExternalCartServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public Result<Void> createClientCart(Long clientId) {
        String url = ecommerceCartServiceUrl + "/ecommerce/carts/create/" + clientId;
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ResponseWrapper> response = restTemplate.exchange(url, HttpMethod.POST, entity, ResponseWrapper.class);
            if (response.getStatusCode() == HttpStatus.CREATED) {
                return Result.success();
            } else {
                return Result.error("Cant Create Cart");
            }
        } catch (Exception e) {
            logger.error("Error occurred while creating cart for client", e);
            return Result.error("Internal Server Error");
        }
    }
}
