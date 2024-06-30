package at.backend.drugstore.microservice.common_models.ExternalService.Adress;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;

@Service
public class ExternalAddressService {

    private final RestTemplate restTemplate;

    @Value("${client.service.url}")
    private String clientServiceUrl;

    @Autowired
    public ExternalAddressService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public Result<AddressDTO> getAddressId(Long addressId) {
        String addressUrl = clientServiceUrl + "/address/" + addressId;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<?> requestEntity = new HttpEntity<>(headers);

        try {
            ResponseEntity<ResponseWrapper<AddressDTO>> responseEntity = restTemplate.exchange(
                    addressUrl,
                    HttpMethod.GET,
                    requestEntity,
                    new ParameterizedTypeReference<ResponseWrapper<AddressDTO>>() {}
            );

            if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND) {
                return new Result<>(false, null, "Address with ID: " + addressId + " not found", HttpStatus.NOT_FOUND);
            } else if (responseEntity.getStatusCode() != HttpStatus.OK) {
                return new Result<>(false, null, "Failed to retrieve address for ID: " + addressId, responseEntity.getStatusCode());
            }

            AddressDTO addressDTO = Objects.requireNonNull(responseEntity.getBody()).getData();
            return Result.success(addressDTO);

        } catch (HttpClientErrorException e) {
            if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                e.printStackTrace();
                return new Result<>(false, null, "Address with ID: " + addressId + " not found", HttpStatus.NOT_FOUND);
            } else {
                return new Result<>(false, null, "Failed to retrieve address for ID: " + addressId, e.getStatusCode());
            }
        } catch (Exception e) {
            return new Result<>(false, null, "Exception occurred while retrieving address for ID: " + addressId + ": " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}
