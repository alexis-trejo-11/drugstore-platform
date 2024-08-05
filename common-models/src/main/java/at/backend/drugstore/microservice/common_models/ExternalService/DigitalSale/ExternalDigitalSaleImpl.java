package at.backend.drugstore.microservice.common_models.ExternalService.DigitalSale;

import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_models.DTO.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class ExternalDigitalSaleImpl {

    private static final Logger log = LoggerFactory.getLogger(ExternalDigitalSaleImpl.class);
    private final RestTemplate restTemplate;

    @Value("${digital_sale.service.url}")
    private String digitalSaleServiceUrl;

    @Autowired
    public ExternalDigitalSaleImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async("taskExecutor")
    public Result<Void> initSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        try {
            String digitalSaleURL = digitalSaleServiceUrl + "/init";
            HttpHeaders headers = new HttpHeaders();
            headers.set("Content-Type", "application/json");

            HttpEntity<DigitalSaleItemInsertDTO> request = new HttpEntity<>(digitalSaleItemInsertDTO, headers);

            log.info("Sending POST request to {}", digitalSaleURL);
            log.info("Request Payload: {}", digitalSaleItemInsertDTO);

            ResponseEntity<ApiResponse<?>> response = restTemplate
                    .exchange(digitalSaleURL,
                            HttpMethod.POST,
                            request,
                            new ParameterizedTypeReference<ApiResponse<?>>() {
                            }
                    );

            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());

            var apiResponse = response.getBody();

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                return new Result<>(true, null, apiResponse.getMessage());
            } else {
                return new Result<>(false, null, apiResponse.getMessage());
            }

        } catch (Exception e) {
            log.error("Error occurred while initiating digital sale: ", e);
            throw new RuntimeException(e);
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<Long> makeDigitalSaleAndGetID(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        String digitalSaleURL = digitalSaleServiceUrl + "/v1/api/digital-sales";
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");

        HttpEntity<DigitalSaleItemInsertDTO> request = new HttpEntity<>(digitalSaleItemInsertDTO, headers);

        log.info("Sending POST request to {}", digitalSaleURL);
        log.info("Request Payload: {}", digitalSaleItemInsertDTO);

        return CompletableFuture.supplyAsync(() ->  {
            ResponseEntity<ApiResponse<DigitalSaleDTO>> response = restTemplate
                    .exchange(digitalSaleURL,
                            HttpMethod.POST,
                            request,
                            new ParameterizedTypeReference<ApiResponse<DigitalSaleDTO>>() {
                            }
                    );

            log.info("Response Status: {}", response.getStatusCode());
            log.info("Response Body: {}", response.getBody());

            var responseStatusCode = response.getStatusCode();
            if (responseStatusCode != HttpStatus.CREATED) {
                throw new RuntimeException();
            }

            return response.getBody().getData().getId();
        });
    }
}