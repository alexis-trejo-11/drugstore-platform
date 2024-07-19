package at.backend.drugstore.microservice.common_models.ExternalService.Payment;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
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
    public PaymentDTO initPayment(PaymentInsertDTO paymentInsertDTO) {
        String url = paymentServiceUrl + "/v1/api/payments/init";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<PaymentInsertDTO> requestEntity = new HttpEntity<>(paymentInsertDTO, headers);

        try {
            ResponseEntity<ApiResponse<PaymentDTO>> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<PaymentDTO>>() {
                    }
            );

            if (responseEntity.getStatusCode() == HttpStatus.CREATED && responseEntity.getBody() != null) {
                return responseEntity.getBody().getData();
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Async
    @Override
    public Result<List<CardDTO>> getCardByClientId(Long clientId) {
        String addressUrl = paymentServiceUrl + "/v1/api/client-cards/client/" + clientId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ApiResponse<List<CardDTO>>> responseEntity = restTemplate.exchange(
                    addressUrl,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ApiResponse<List<CardDTO>>>() {}
            );

            if (responseEntity.getStatusCode()  == HttpStatus.NOT_FOUND && responseEntity.getBody() != null) {
                return new Result<>(false, null, "Can't Get Addresses");
            }

            List<CardDTO> addressDTO = Objects.requireNonNull(responseEntity.getBody()).getData();
            return Result.success(addressDTO);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }


}
