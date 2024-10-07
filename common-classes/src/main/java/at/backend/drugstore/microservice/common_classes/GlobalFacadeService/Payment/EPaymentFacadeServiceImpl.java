package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Payment;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Service
@Slf4j
public class EPaymentFacadeServiceImpl implements EPaymentFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> paymentServiceUrlProvider;

    public EPaymentFacadeServiceImpl(RestTemplate restTemplate, @Qualifier("ePaymentServiceUrlProvider") Supplier<String> paymentServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.paymentServiceUrlProvider = paymentServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<PaymentDTO> initPayment(PaymentInsertDTO paymentInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            String url = paymentServiceUrlProvider.get() + "/v1/drugstore/ecommerce-payments/init";
            log.info("Initializing payment with URL: {}", url);
            log.debug("PaymentInsertDTO: {}", paymentInsertDTO);

            HttpHeaders headers = new HttpHeaders();
            headers.set(HttpHeaders.CONTENT_TYPE, "application/json");
            HttpEntity<PaymentInsertDTO> requestEntity = new HttpEntity<>(paymentInsertDTO, headers);

            ResponseEntity<ResponseWrapper<PaymentDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseWrapper<PaymentDTO>>() {}
            );
            ResponseWrapper<PaymentDTO> responseEntityBody = responseEntity.getBody();

            assert responseEntityBody != null;
            log.info("Payment initialization successful with status code: {}",responseEntityBody.getStatusCode());
            log.info("Payment initialization successful with Id: {}", responseEntityBody.getData().getId());

            return responseEntityBody.getData();
        });

    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<List<CardDTO>>> getCardByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            String url = paymentServiceUrlProvider.get() + "/v1/drugstore/ecommerce-payments/cards/by-client/" + clientId;
            log.info("Fetching client cards with URL: {}", url);

            ResponseEntity<ResponseWrapper<List<CardDTO>>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    new ParameterizedTypeReference<ResponseWrapper<List<CardDTO>>>() {}
            );
            ResponseWrapper<List<CardDTO>> responseEntityBody = responseEntity.getBody();
            assert responseEntityBody != null;

            if (HttpStatus.NOT_FOUND.value() == responseEntityBody.getStatusCode()) {
                return new Result<>(false, null, "Can't Get Cards");
            }

            List<CardDTO> cardDTOs = responseEntityBody.getData();
            return Result.success(cardDTOs);
        });
    }
}
