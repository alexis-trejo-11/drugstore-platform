package at.backend.drugstore.microservice.common_models.ExternalService.Client;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Objects;


@Service
public class ExternalClientServiceImpl implements  ExternalClientService {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(ExternalClientServiceImpl.class);

    @Value("${client.service.url}")
    private String clientServiceUrl;

    @Autowired
    public ExternalClientServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Async
    public Result<ClientDTO> createClient(ClientInsertDTO clientInsertDTO) {
        String url = clientServiceUrl + "/clients/add";
        try {
            ResponseEntity<ApiResponse<ClientDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(clientInsertDTO),
                    new ParameterizedTypeReference<ApiResponse<ClientDTO>>() {}
            );

             if (response.getStatusCode() != HttpStatus.CREATED) {
                 return Result.success(Objects.requireNonNull(response.getBody()).getData());
             } else {
                 return new Result<>(false, null, Objects.requireNonNull(response.getBody()).getMessage());
             }
        } catch (Exception e) {
            logger.error("Error occurred while creating client", e);
            throw new RuntimeException(e);
        }
    }


    public Result<ClientDTO> findClientById(Long clientId) {
        String url = clientServiceUrl + "/clients/" + clientId;
        try {
            ResponseEntity<ApiResponse<ClientDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ApiResponse<ClientDTO>>() {
                    }
            );
            ApiResponse<ClientDTO> responseBody = response.getBody();
            HttpStatus statusCode = response.getStatusCode();

                // Handle Status Code using switch statement
                if (statusCode == HttpStatus.OK) {
                    assert responseBody != null;
                    return new Result<>(true, responseBody.getData(), responseBody.getMessage());

                } else if (statusCode == HttpStatus.NOT_FOUND) {
                    assert responseBody != null;
                    return new Result<>(false, null, responseBody.getMessage());
                } else {
                    assert responseBody != null;
                    return new Result<>(false, null, responseBody.getMessage());
                }
        } catch (Exception e) {
            logger.error("Error occurred while finding client", e);
            throw  new RuntimeException(e);
        }
    }
}