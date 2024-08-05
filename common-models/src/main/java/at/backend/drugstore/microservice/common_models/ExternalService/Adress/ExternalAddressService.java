package at.backend.drugstore.microservice.common_models.ExternalService.Adress;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ExternalAddressService {

    private final RestTemplate restTemplate;

    @Value("${client.service.url}")
    private String clientServiceUrl;

    @Autowired
    public ExternalAddressService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async("taskExecutor")
    public CompletableFuture<Result<AddressDTO>> getAddressId(Long addressId) {
        String addressUrl = clientServiceUrl + "/v1/api/clients/address/" + addressId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            log.info("Fetching address with ID: {}", addressId);

            return CompletableFuture.supplyAsync(() -> {
                ResponseEntity<ApiResponse<AddressDTO>> responseEntity = restTemplate.exchange(
                        addressUrl,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<ApiResponse<AddressDTO>>() {}
                );

                if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                    log.warn("Address with ID: {} not found", addressId);
                    return new Result<>(false, null, "Address with ID: " + addressId + " not found");
                }

                AddressDTO addressDTO = Objects.requireNonNull(responseEntity.getBody()).getData();
                log.info("Address with ID: {} successfully fetched", addressId);
                return Result.success(addressDTO);
            });
        } catch (Exception e) {
            log.error("Error fetching address with ID: {}", addressId, e);
            throw new RuntimeException(e);
        }
    }

    @Async("taskExecutor")
    public CompletableFuture<Result<List<AddressDTO>>> getAddressByClientId(Long clientId) {
        String addressUrl = clientServiceUrl + "/v1/api/clients/address/client/" + clientId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            return CompletableFuture.supplyAsync(() -> {
                ResponseEntity<ApiResponse<List<AddressDTO>>> responseEntity = restTemplate.exchange(
                        addressUrl,
                        HttpMethod.GET,
                        requestEntity,
                        new ParameterizedTypeReference<ApiResponse<List<AddressDTO>>>() {}
                );

                if (responseEntity.getStatusCode()  == HttpStatus.NOT_FOUND &&  responseEntity.getBody() != null) {
                    return new Result<>(false, null, "Can't Get Addresses");
                }
                List<AddressDTO> addressDTO = Objects.requireNonNull(responseEntity.getBody()).getData();
                return Result.success(addressDTO);
            });
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
