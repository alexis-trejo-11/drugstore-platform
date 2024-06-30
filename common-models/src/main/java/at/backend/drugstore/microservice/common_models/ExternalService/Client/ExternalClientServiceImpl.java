package at.backend.drugstore.microservice.common_models.ExternalService.Client;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
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
import org.springframework.web.client.HttpClientErrorException;
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
    public ResponseEntity<Result<ClientDTO>> createClient(ClientInsertDTO clientInsertDTO) {
        String url = clientServiceUrl + "/clients/add";
        try {
            ResponseEntity<ResponseWrapper<ClientDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.POST,
                    new HttpEntity<>(clientInsertDTO),
                    new ParameterizedTypeReference<ResponseWrapper<ClientDTO>>() {}
            );

            if (response.getStatusCode() != HttpStatus.CREATED) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Result.error("Cannot create client: " + response.getStatusCode().getReasonPhrase()));
            } else {
                ResponseWrapper<ClientDTO> responseBody = response.getBody();
                if (responseBody != null && responseBody.getData() != null) {
                    return ResponseEntity.status(response.getStatusCode()).body(Result.success(responseBody.getData()));
                } else if (responseBody != null && responseBody.getMessage() != null) {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error("Client service error: " + responseBody.getMessage()));
                } else {
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error("Invalid response from client service"));
                }
            }
        } catch (Exception e) {
            logger.error("Error occurred while creating client", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Result.error("Internal server error: " + e.getMessage()));
        }
    }


    public Result<ClientDTO> findClientById(Long clientId) {
        String url = clientServiceUrl + "/" + clientId;
        try {
            ResponseEntity<ResponseWrapper<ClientDTO>> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    null,
                    new ParameterizedTypeReference<ResponseWrapper<ClientDTO>>() {
                    }
            );

            ResponseWrapper<ClientDTO> responseBody = response.getBody();
            HttpStatus statusCode = response.getStatusCode();

            if (statusCode == HttpStatus.OK && responseBody != null && responseBody.getData() != null) {
                return new Result<>(true, responseBody.getData(), null, HttpStatus.OK);
            } else if (responseBody != null) {
                return new Result<>(false, null, responseBody.getMessage(), statusCode);
            } else {
                return new Result<>(false, null, "An error occurred", statusCode);
            }
        } catch (HttpClientErrorException.NotFound e) {
            logger.error("Client with ID " + clientId + " not found", e);
            return new Result<>(false, null, "Client with ID " + clientId + " not found", HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            logger.error("Error occurred while finding client", e);
            return new Result<>(false, null, "Internal server error: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}