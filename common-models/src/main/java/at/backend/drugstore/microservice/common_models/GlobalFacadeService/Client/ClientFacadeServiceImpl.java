package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client;

import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Service
public class ClientFacadeServiceImpl implements ClientFacadeService {

    private final RestTemplate restTemplate;
    private final Logger logger = LoggerFactory.getLogger(ClientFacadeServiceImpl.class);

    private final String clientServiceUrl = "http://client-service:8081" ;

    public ClientFacadeServiceImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<ClientDTO> createClient(ClientInsertDTO clientInsertDTO) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = clientServiceUrl + "/v1/api/clients/add";
                ResponseEntity<ResponseWrapper> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(clientInsertDTO),
                        ResponseWrapper.class
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    ResponseWrapper<ClientDTO> responseBody = (ResponseWrapper<ClientDTO>) responseEntity.getBody();
                    if (responseBody != null) {
                        logger.info("Client created successfully");
                        return responseBody.getData();
                    } else {
                        logger.error("Response body is null");
                        throw new RuntimeException("Client creation failed");
                    }
                } else {
                    String errorMessage = String.format("Failed to create client, status code: %s",
                            responseEntity.getStatusCode());
                    logger.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
            } catch (Exception e) {
                logger.error("Error occurred while creating client", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<ClientDTO>> findClientById(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = clientServiceUrl + "/v1/api/clients/" + clientId;
                ResponseEntity<ResponseWrapper> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        ResponseWrapper.class
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    ResponseWrapper<ClientDTO> responseBody = (ResponseWrapper<ClientDTO>) responseEntity.getBody();
                    if (responseBody != null) {
                        logger.info("Client found successfully: {}", clientId);
                        return new Result<>(true, responseBody.getData(), responseBody.getMessage());
                    } else {
                        logger.warn("Client not found: {}, status code: {}", clientId, responseEntity.getStatusCode());
                        return new Result<>(false, null, "Client not found");
                    }
                } else {
                    String message = responseEntity.getBody() != null ? ((ResponseWrapper<ClientDTO>) responseEntity.getBody()).getMessage() : "Client not found";
                    logger.warn("Client not found: {}, status code: {}", clientId, responseEntity.getStatusCode());
                    return new Result<>(false, null, message);
                }
            } catch (Exception e) {
                logger.error("Error occurred while finding client", e);
                return new Result<>(false, null, "An error occurred");
            }
        });
    }
}
