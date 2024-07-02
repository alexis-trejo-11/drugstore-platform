package at.backend.drugstore.microservice.common_models.ExternalService.Payment;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;

@Service
public class ExternalPaymentServiceServiceImpl implements ExternalPaymentService {

    private final RestTemplate restTemplate;

    @Value("${ecommerce.payment.service.url}")
    private String paymentServiceUrl;

    @Autowired
    public ExternalPaymentServiceServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Result<Void> initPayment(PaymentInsertDTO paymentInsertDTO) {
        String url = paymentServiceUrl + "/payment/init";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentInsertDTO> requestEntity = new HttpEntity<>(paymentInsertDTO, headers);

        try {
            ResponseEntity<ResponseWrapper<Void>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseWrapper<Void>>() {
                    }
            );

            if (responseEntity.getStatusCode() == HttpStatus.CREATED && responseEntity.getBody() != null) {
                return Result.success();
            } else {
                return new Result<>(false, null, "Failed to init payment, status code: " + responseEntity.getStatusCode(), responseEntity.getStatusCode());
            }
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("Exception occurred while creating order: " + e.getMessage());
        }
    }

    @Async
    @Override
    public Result<List<CardDTO>> getCardByClientId(Long clientId) {
        String addressUrl = paymentServiceUrl + "/cards/client/" + clientId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseWrapper<List<CardDTO>>> responseEntity = restTemplate.exchange(
                    addressUrl,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseWrapper<List<CardDTO>>>() {}
            );

            if (responseEntity.getStatusCode()  != HttpStatus.OK) {
                return new Result<>(false, null, "Can't Get Addresses");
            }

            List<CardDTO> addressDTO = Objects.requireNonNull(responseEntity.getBody()).getData();
            return Result.success(addressDTO);
        } catch (Exception e) {
            e.printStackTrace();
            return new Result<>(false, null, "Exception occurred while retrieving cards");
        }
    }


}
