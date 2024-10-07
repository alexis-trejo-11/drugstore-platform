package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Address;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

@Slf4j
@Service
public class AddressFacadeServiceImpl implements AddressFacadeService {

    private final RestTemplate restTemplate;
    private final Supplier<String> addressServiceUrlProvider;

    @Autowired
    public AddressFacadeServiceImpl(RestTemplate restTemplate, Supplier<String> addressServiceUrlProvider) {
        this.restTemplate = restTemplate;
        this.addressServiceUrlProvider = addressServiceUrlProvider;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<AddressDTO>> getAddressById(Long addressId) {
        String addressUrl = addressServiceUrlProvider.get() + "/v1/drugstore/addresses/" + addressId;
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
        String addressUrl = addressServiceUrlProvider.get() + "/v1/drugstore/addresses/client/" + clientId;
        return CompletableFuture.supplyAsync(() -> {
            HttpHeaders headers = new HttpHeaders();
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<ResponseWrapper<List<AddressDTO>>> responseEntity = restTemplate.exchange(
                        addressUrl,
                        HttpMethod.GET,
                        entity,
                        new ParameterizedTypeReference<ResponseWrapper<List<AddressDTO>>>() {});

                if (responseEntity.getStatusCode() == HttpStatus.NOT_FOUND && responseEntity.getBody() != null) {
                    log.warn("getAddressesByClientId -> Client ID {} not found", clientId);
                    return new Result<>(false, null, "client with ID " + clientId + " not found.");
                }

            assert responseEntity.getBody() != null;
            List<AddressDTO> addressDTOs = responseEntity.getBody().getData();

            log.info("getAddressesByClientId -> Addresses for client ID: {} successfully fetched", clientId);
            return Result.success(addressDTOs);
        });
    }
}