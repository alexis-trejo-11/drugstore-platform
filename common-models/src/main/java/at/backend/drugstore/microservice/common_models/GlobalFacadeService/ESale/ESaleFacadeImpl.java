package at.backend.drugstore.microservice.common_models.GlobalFacadeService.ESale;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class ESaleFacadeImpl implements ESaleFacadeService {

    private static final Logger log = LoggerFactory.getLogger(ESaleFacadeImpl.class);
    private final RestTemplate restTemplate;

    private String digitalSaleServiceUrl = "http://10.212.82.114:8089";

    public ESaleFacadeImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<Void>> initSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = digitalSaleServiceUrl + "/init";
                log.info("Sending POST request to {}", url);
                log.info("Request Payload: {}", digitalSaleItemInsertDTO);

                HttpEntity<DigitalSaleItemInsertDTO> requestEntity = new HttpEntity<>(digitalSaleItemInsertDTO);
                ResponseEntity<ResponseWrapper> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        ResponseWrapper.class
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    ResponseWrapper<Void> responseWrapper = (ResponseWrapper<Void>) responseEntity.getBody();
                    if (responseWrapper != null && responseWrapper.isSuccess()) {
                        return new Result<>(true, null, responseWrapper.getMessage());
                    } else {
                        return new Result<>(false, null, responseWrapper != null ? responseWrapper.getMessage() : "Unknown error");
                    }
                } else {
                    String errorMessage = "Error response from Digital Sale Service: " + responseEntity.getStatusCode();
                    log.error(errorMessage);
                    return new Result<>(false, null, errorMessage);
                }
            } catch (Exception e) {
                log.error("Error occurred while initiating digital sale: ", e);
                return new Result<>(false, null, "Exception occurred: " + e.getMessage());
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Long> makeDigitalSaleAndGetID(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = digitalSaleServiceUrl + "/v1/api/digital-sales";
                log.info("Sending POST request to {}", url);
                log.info("Request Payload: {}", digitalSaleItemInsertDTO);

                HttpEntity<DigitalSaleItemInsertDTO> requestEntity = new HttpEntity<>(digitalSaleItemInsertDTO);
                ResponseEntity<ResponseWrapper> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        ResponseWrapper.class
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    ResponseWrapper<DigitalSaleDTO> responseWrapper = (ResponseWrapper<DigitalSaleDTO>) responseEntity.getBody();
                    if (responseWrapper != null && responseWrapper.getData() != null) {
                        if (responseWrapper.isSuccess()) {
                            return responseWrapper.getData().getId();
                        } else {
                            throw new RuntimeException("Failed to create digital sale: " + responseWrapper.getMessage());
                        }
                    } else {
                        throw new RuntimeException("Failed to create digital sale: No data received");
                    }
                } else {
                    String errorMessage = "Error response from Digital Sale Service: " + responseEntity.getStatusCode();
                    log.error(errorMessage);
                    throw new RuntimeException("Failed to create digital sale: " + errorMessage);
                }
            } catch (Exception e) {
                log.error("Error occurred while making digital sale: ", e);
                throw new RuntimeException("Exception occurred: " + e.getMessage(), e);
            }
        });
    }
}
