package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Payment;

import at.backend.drugstore.microservice.common_models.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

@Service
public class EPaymentServiceFacadeServiceImpl implements EPaymentFacadeService {

    private static final Logger logger = LoggerFactory.getLogger(EPaymentServiceFacadeServiceImpl.class);
    private final RestTemplate restTemplate;

    private final String paymentServiceUrl = "http://10.212.82.114:8090";

    public EPaymentServiceFacadeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<PaymentDTO> initPayment(PaymentInsertDTO paymentInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            String url = paymentServiceUrl + "/v1/api/payments/init";
            logger.info("Initializing payment with URL: {}", url);
            logger.debug("PaymentInsertDTO: {}", paymentInsertDTO);

            try {
                HttpHeaders headers = new HttpHeaders();
                headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
                HttpEntity<PaymentInsertDTO> requestEntity = new HttpEntity<>(paymentInsertDTO, headers);

                ResponseEntity<ResponseWrapper<PaymentDTO>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        requestEntity,
                        new ParameterizedTypeReference<ResponseWrapper<PaymentDTO>>() {}
                );

                ResponseWrapper<PaymentDTO> response = responseEntity.getBody();

                if (response != null && response.getStatusCode() == HttpStatus.CREATED.value()) {
                    logger.info("Payment initialization successful with status code: {}", HttpStatus.CREATED);
                    return response.getData();
                } else {
                    logger.warn("Payment initialization failed with status code: {}", HttpStatus.BAD_REQUEST);
                    return null;
                }
            } catch (Exception e) {
                logger.error("Exception occurred during payment initialization", e);
                throw new CompletionException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<List<CardDTO>>> getCardByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            String url = paymentServiceUrl + "/v1/api/client-cards/client/" + clientId;
            logger.info("Fetching client cards with URL: {}", url);

            try {
                ResponseEntity<ResponseWrapper<List<CardDTO>>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseWrapper<List<CardDTO>>>() {}
                );

                ResponseWrapper<List<CardDTO>> response = responseEntity.getBody();

                if (response != null && HttpStatus.NOT_FOUND.value() == response.getStatusCode()) {
                    return new Result<>(false, null, "Can't Get Cards");
                }

                List<CardDTO> cardDTOs = Objects.requireNonNull(response).getData();
                return Result.success(cardDTOs);
            } catch (Exception e) {
                logger.error("An unexpected error occurred while retrieving cards for client ID {}: {}", clientId, e.getMessage());
                throw new CompletionException(e);
            }
        });
    }
}
