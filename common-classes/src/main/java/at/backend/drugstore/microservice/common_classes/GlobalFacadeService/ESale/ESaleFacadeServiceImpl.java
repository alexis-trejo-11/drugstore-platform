package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.ESale;

import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.DigitalSaleItemInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
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
    public CompletableFuture<Result<Void>> initSale(DigitalSaleItemInsertDTO digitalSaleItemInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = eSaleServiceUrlProvider.get() + "/init";
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
                String url = eSaleServiceUrlProvider.get() + "/v1/api/digital-sales";
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
