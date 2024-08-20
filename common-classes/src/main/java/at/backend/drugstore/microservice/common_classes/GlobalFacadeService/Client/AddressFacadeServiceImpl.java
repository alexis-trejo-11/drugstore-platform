package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
public class AddressFacadeServiceImpl implements AddressFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> clientServiceUrlProvider;

    @Autowired
    public AddressFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> clientServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.clientServiceUrlProvider = clientServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<AddressDTO>> getAddressById(Long addressId) {
        String addressUrl = clientServiceUrlProvider.get() + "/v1/api/clients/address/" + addressId;
        return CompletableFuture.supplyAsync(() -> {
                ResponseEntity<ResponseWrapper<AddressDTO>> responseEntity = restTemplate.exchange(
                        addressUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseWrapper<AddressDTO>>() {});

                if (responseEntity.getStatusCode().is4xxClientError() && responseEntity.getBody() != null) {
                    log.warn("Address with ID: {} not found", addressId);
                    return new Result<>(false, null, responseEntity.getBody().getMessage());
                }

            AddressDTO addressDTO = Objects.requireNonNull(responseEntity.getBody()).getData();
            log.info("Address with ID: {} successfully fetched", addressId);
            return Result.success(addressDTO);
        });
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<List<AddressDTO>>> getAddressesByClientId(Long clientId) {
        String addressUrl = clientServiceUrlProvider.get() + "/v1/api/clients/address/client/" + clientId;
        return CompletableFuture.supplyAsync(() -> {
                ResponseEntity<ResponseWrapper<List<AddressDTO>>> responseEntity = restTemplate.exchange(
                        addressUrl,
                        HttpMethod.GET,
                        null,
                        new ParameterizedTypeReference<ResponseWrapper<List<AddressDTO>>>() {});

                if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND && responseEntity.getBody() != null) {
                    log.warn("Client ID {} not found", clientId);
                    return new Result<>(false, null, "client with ID " + clientId + " not found.");
                }

            assert responseEntity.getBody() != null;
            List<AddressDTO> addressDTOs = responseEntity.getBody().getData();

            log.info("Addresses for client ID: {} successfully fetched", clientId);
            return Result.success(addressDTOs);
        });
    }
}