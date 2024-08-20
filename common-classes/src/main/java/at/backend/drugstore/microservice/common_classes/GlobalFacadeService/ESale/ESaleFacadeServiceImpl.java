package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
public class ESaleFacadeServiceImpl implements ESaleFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> eSaleServiceUrlProvider;

    public ESaleFacadeServiceImpl(RestTemplate restTemplate,
                                  Supplier<String> eSaleServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.eSaleServiceUrlProvider = eSaleServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Long> makeDigitalSaleAndGetID(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            String url = eSaleServiceUrlProvider.get() + "/v1/api/digital-sales";
            log.info("Making digital sale with URL: {}", url);
            log.info("Request Payload: {}", digitalSaleItemInsertDTO);

            ResponseEntity<ResponseWrapper<DigitalSaleDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(digitalSaleItemInsertDTO),
                    new ParameterizedTypeReference<ResponseWrapper<DigitalSaleDTO>>() {}
            );

            ResponseWrapper<DigitalSaleDTO> responseEntityBody = responseEntity.getBody();
            log.debug("Response received: {}", responseEntityBody);

            assert responseEntityBody != null;

            if (responseEntity.getStatusCode() == HttpStatus.BAD_REQUEST) {
                log.error("Failed to make digital sale. Error: {}", responseEntityBody.getMessage());
                throw new RuntimeException(responseEntityBody.getMessage());
            }

            Long saleId = responseEntityBody.getData().getId();
            log.info("Digital sale made successfully with ID: {}", saleId);
            return saleId;
        });
    }
}
