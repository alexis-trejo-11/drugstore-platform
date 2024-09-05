package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.client_service.Service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.client_service.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/drugstore/addresses")
@Tag(name = "Drugstore Microservice API (Client Service)", description = "Service for managing client addressees")
public class AddressController {

    private final AddressService addressService;
    private final ClientService clientService;

    @Autowired
    public AddressController(AddressService addressService, ClientService clientService) {
        this.addressService = addressService;
        this.clientService = clientService;
    }

    // Completable Future Commonly User to Connect Service Each Other

    @Operation(summary = "Get address by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/{addressId}")
    public CompletableFuture<ResponseWrapper<AddressDTO>> getAddressById(@PathVariable Long addressId) {
        log.info("Request to get address with addressId: {}", addressId);
        return addressService.getAddressById(addressId)
                .thenApply(optionalAddressDTO -> {
                    if (optionalAddressDTO.isEmpty()) {
                        log.info("Address found for addressId: {}", addressId);
                        return ResponseWrapper.notFound("Address", "ID");
                    }
                    log.warn("Address not found for addressId: {}", addressId);
                    return ResponseWrapper.found(optionalAddressDTO.get(), "Address");
                });
    }


    @Operation(summary = "Get all addresses by client ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addresses successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/client/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<AddressDTO>>>> getAddressesByClientId(@PathVariable Long clientId) {
        log.info("Request to get addresses for clientId: {}", clientId);

        boolean isClientExisting = clientService.validateExistingClient(clientId);
        if (!isClientExisting) {
            log.error("getAddressesByClientId -> Failed to get addresses for clientId: {}.", clientId);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(ResponseWrapper.notFound("Address", "Client ID")));
        }

        return addressService.getAddressesByClientId(clientId)
                .thenApply(addressDTOS -> {
                    log.info("getAddressesByClientId -> Addresses successfully fetched for clientId: {}", clientId);
                    return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(addressDTOS, "Client Addresses"));
                });
    }


    @Operation(summary = "Add a new address for a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address successfully created"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/add")
    public ResponseEntity<ResponseWrapper<Void>> insertAddress(@Valid @RequestBody AddressInsertDTO addressInsertDTO, @RequestParam Long clientID) {
        log.info("Request to add address for clientId: {}", clientID);

        Result<Void> addResult = addressService.addAddress(addressInsertDTO, clientID);
        if (!addResult.isSuccess()) {
            log.error("Failed to add address for clientID: {}. Error: {}", clientID, addResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(addResult.getErrorMessage()));
        }

        log.info("Address successfully created for clientID: {}", clientID);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created("Address"));
    }


    @Operation(summary = "Delete address by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteAddressById(@PathVariable Long addressId) {
        log.info("Delete Address: Request to delete address with addressId: {}", addressId);

        boolean isAddressExisting = addressService.validateExistingAddress(addressId);
        if (!isAddressExisting) {
            log.error("Delete Address: Failed to found address with ID: {}", addressId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Address", "ID"));
        }


        log.info("Delete Address: Address successfully deleted with addressId: {}", addressId);
        addressService.deleteAddressById(addressId);

        return ResponseEntity.ok(ResponseWrapper.ok("Address", "Delete"));
    }
}
