package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import microservice.client_service.Service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/clients/address")
@Tag(name = "Drugstore Microservice API (Client Service)", description = "Service for managing client addressees")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    @Operation(summary = "Add a new address for a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address successfully created"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> insertAddress(@Valid @RequestBody AddressInsertDTO addressInsertDTO,
                                                                                  @RequestParam Long clientId) {
        log.info("Request to add address for clientId: {}", clientId);
        return addressService.addAddress(addressInsertDTO, clientId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        log.error("Failed to add address for clientId: {}. Error: {}", clientId, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, result.getErrorMessage(), 404));
                    }
                    log.info("Address successfully created for clientId: {}", clientId);
                    return ResponseEntity.status(HttpStatus.CREATED)
                            .body(new ResponseWrapper<>(true, null, "Address Successfully Created!.", 201));
                });
    }

    @Operation(summary = "Get address by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @GetMapping("{addressId}")
    public CompletableFuture<ResponseWrapper<AddressDTO>> getAddressById(@PathVariable Long addressId) {
        log.info("Request to get address with addressId: {}", addressId);
        return addressService.getAddressById(addressId)
                .thenApply(optionalAddressDTO -> {
                    if (optionalAddressDTO.isPresent()) {
                        log.info("Address found for addressId: {}", addressId);
                        return new ResponseWrapper<>(true, optionalAddressDTO.get(), "Address Successfully Fetched", 200);
                    } else {
                        log.warn("Address not found for addressId: {}", addressId);
                        return new ResponseWrapper<>(false, null, "Address with Id " + addressId + " Not Found", 404);
                    }
                });
    }


    @Operation(summary = "Get all addresses by client ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addresses successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("client/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<AddressDTO>>>> getAddressesByClientId(@PathVariable Long clientId) {
        log.info("Request to get addresses for clientId: {}", clientId);
        return addressService.getAddressesByClientId(clientId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        log.error("Failed to get addresses for clientId: {}. Error: {}", clientId, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, result.getErrorMessage(), 404));
                    }
                    log.info("Addresses successfully fetched for clientId: {}", clientId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, result.getData(), "Addresses Correctly Fetched", 200));
                });
    }


    @Operation(summary = "Update address by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully updated"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @PutMapping("client/address/update/{addressId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> updateAddressById(@RequestBody AddressInsertDTO addressInsertDTO, @PathVariable Long addressId) {
        log.info("Request to update address with addressId: {}", addressId);
        return addressService.updateAddressById(addressInsertDTO, addressId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        log.error("Failed to update address with addressId: {}. Error: {}", addressId, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, result.getErrorMessage(), 404));
                    }
                    log.info("Address successfully updated with addressId: {}", addressId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, null, "Address Successfully Updated.", 200));
                });
    }


    @Operation(summary = "Delete address by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Address not found")
    })
    @DeleteMapping("client/address/remove/{addressId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteAddressById(@PathVariable Long addressId) {
        log.info("Request to delete address with addressId: {}", addressId);
        return addressService.deleteAddressById(addressId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        log.error("Failed to delete address with addressId: {}. Error: {}", addressId, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                                .body(new ResponseWrapper<>(false, null, result.getErrorMessage(), 404));
                    }
                    log.info("Address successfully deleted with addressId: {}", addressId);
                    return ResponseEntity.status(HttpStatus.OK)
                            .body(new ResponseWrapper<>(true, null, result.getData(), 200));
                });
    }
}
