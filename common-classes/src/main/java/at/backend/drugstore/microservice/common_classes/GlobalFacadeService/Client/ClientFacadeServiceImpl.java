package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;

import java.util.function.Supplier;

import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class ClientFacadeServiceImpl implements ClientFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> clientServiceUrlProvider;

    public ClientFacadeServiceImpl(RestTemplate restTemplate,
                                   Supplier<String> clientServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.clientServiceUrlProvider = clientServiceUrlProvider;
    }


    @Override
    @Async("taskExecutor")
    public CompletableFuture<ClientDTO> createClient(ClientInsertDTO clientInsertDTO) {
         return CompletableFuture.supplyAsync(() -> {
            String url = clientServiceUrlProvider.get() + "/v1/drugstore/clients/create";
            log.info("Sending client data to URL: {}", url);

                ResponseEntity<ResponseWrapper<ClientDTO>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.POST,
                        new HttpEntity<>(clientInsertDTO),
                        new ParameterizedTypeReference<ResponseWrapper<ClientDTO>>() {}
                );


                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    ResponseWrapper<ClientDTO> responseBody = responseEntity.getBody();
                    if (responseBody != null) {
                        log.info("Client created successfully");
                        return responseBody.getData();
                    } else {
                        log.error("Response body is null");
                        throw new RuntimeException("Client creation failed");
                    }
                } else {
                    String errorMessage = String.format("Failed to create client, status code: %s",
                            responseEntity.getStatusCode());
                    log.error(errorMessage);
                    throw new RuntimeException(errorMessage);
                }
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<ClientDTO> getClientById(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            try {
                String url = clientServiceUrlProvider.get() + "/v1/drugstore/clients/" + clientId;
                ResponseEntity<ResponseWrapper<ClientDTO>> responseEntity = restTemplate.exchange(
                        url,
                        HttpMethod.GET,
                        HttpEntity.EMPTY,
                        new ParameterizedTypeReference<ResponseWrapper<ClientDTO>>() {}
                );

                if (responseEntity.getStatusCode().is2xxSuccessful()) {
                    ResponseWrapper<ClientDTO> responseBody = responseEntity.getBody();
                        log.info("getClientById -> Client found successfully: {}", responseBody);
                        return responseBody.getData();
                } else {
                    log.warn("getClientById -> Client not found: {}, status code: {}", clientId, responseEntity.getStatusCode());
                    return null;
                }
            } catch (Exception e) {
                log.error("Error occurred while finding client", e);
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    @Async("taskExecutor")
    public boolean validateExistingClient(Long clientId) {
            String url = clientServiceUrlProvider.get() + "/v1/drugstore/clients/validate/" + clientId;

            ResponseEntity<Boolean> responseEntity = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    HttpEntity.EMPTY,
                    Boolean.class
            );
            return Boolean.TRUE.equals(responseEntity.getBody());
        }
    }
