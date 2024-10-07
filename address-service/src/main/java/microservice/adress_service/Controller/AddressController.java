package microservice.adress_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.adress_service.Service.ClientAddressService;
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
@RequestMapping("v1/drugstore/addresses")
@Tag(name = "Drugstore Microservice API (Client Service)", description = "Service for managing client addressees")
public class AddressController {

    private final ClientAddressService clientAddressService;

    @Autowired
    public AddressController(ClientAddressService clientAddressService) {
        this.clientAddressService = clientAddressService;
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
        return clientAddressService.getAddressById(addressId)
                .thenApply(optionalAddressDTO -> optionalAddressDTO.map(addressDTO -> ResponseWrapper.found(addressDTO, "Address"))
                        .orElseGet(() -> ResponseWrapper.notFound("Address")));
    }


    @Operation(summary = "Get all addresses by client ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Addresses successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @GetMapping("/client/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<AddressDTO>>>> getAddressesByClientId(@PathVariable Long clientId) {
        return clientAddressService.getAddressesByClientId(clientId)
                .thenApply(addressDTOS -> ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(addressDTOS, "Client Addresses")));
    }


    @Operation(summary = "Add a new address for a client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address successfully created"),
            @ApiResponse(responseCode = "404", description = "Client not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @PostMapping("/add")
    public ResponseEntity<ResponseWrapper<Void>> insertAddress(@Valid @RequestBody AddressInsertDTO addressInsertDTO, @RequestParam Long clientID) {
        Result<Void> addResult = clientAddressService.addAddress(addressInsertDTO, clientID);
        if (!addResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(addResult.getErrorMessage()));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created("Address"));
    }


    @Operation(summary = "Delete address by Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Address not found"),
            @ApiResponse(responseCode = "500", description = "Internal Server Error")
    })
    @DeleteMapping("/delete/{addressId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteAddressById(@PathVariable Long addressId,  @RequestParam Long clientId) {
        clientAddressService.deleteAddressFromClient(addressId, clientId);

        return ResponseEntity.ok(ResponseWrapper.ok("Address", "Delete"));
    }
}
