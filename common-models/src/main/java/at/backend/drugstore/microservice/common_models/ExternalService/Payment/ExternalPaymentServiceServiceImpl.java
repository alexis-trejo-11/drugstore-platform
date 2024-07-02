package at.backend.drugstore.microservice.common_models.ExternalService.Payment;

import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class ExternalPaymentServiceImpl implements ExternalPayment {

    private final RestTemplate restTemplate;

    @Value("${ecommerce.payment.service.url}")
    private String paymentServiceUrl;

    @Autowired
    public ExternalPaymentServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public Result<Void> initPayment(PaymentInsertDTO paymentInsertDTO) {
        String url = paymentServiceUrl + "/init";
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
}
