package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.Adress.AddressUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.Security.AuthSecurity;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import microservice.client_service.Service.AddressService;
import microservice.client_service.Service.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequestMapping("v1/drugstore/client-address")
public class AddressClientController {

    private final AddressService addressService;
    private final ClientService clientService;
    private final AuthSecurity authSecurity;

    @Autowired
    public AddressClientController(AddressService addressService,
                                   ClientService clientService,
                                   AuthSecurity authSecurity) {
        this.addressService = addressService;
        this.clientService = clientService;
        this.authSecurity = authSecurity;
    }

    @Operation(summary = "Fetch all addresses of the current client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found addresses",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = AddressDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Addresses not found",
                    content = @Content)
    })
    @GetMapping("/addresses")
    public ResponseEntity<ResponseWrapper<List<AddressDTO>>> getMyAddresses(HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("Fetching addresses for client ID: {}", clientId);

        CompletableFuture<List<AddressDTO>> addressDTOsAsync = addressService.getAddressesByClientId(clientId);
        List<AddressDTO> addressDTOs = addressDTOsAsync.join();

        log.info("Addresses successfully fetched for client ID: {}", clientId);
        return ResponseEntity.ok(ResponseWrapper.found(addressDTOs, "Addresses"));
    }

    @Operation(summary = "Fetch current client data")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Found client data",
                    content = { @Content(mediaType = "application/json",
                            schema = @Schema(implementation = ClientDTO.class)) }),
            @ApiResponse(responseCode = "404", description = "Client not found",
                    content = @Content)
    })
    @GetMapping("/client-data")
    public ResponseEntity<ResponseWrapper<ClientDTO>> getMyClientData(HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("Fetching client data for client ID: {}", clientId);

        ClientDTO clientDTO = clientService.getClientById(clientId);

        log.info("Client data successfully fetched for client ID: {}", clientId);
        return ResponseEntity.ok(ResponseWrapper.found(clientDTO, "Client"));
    }

    @Operation(summary = "Insert a new address for the current client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address created",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Failed to create address",
                    content = @Content)
    })
    @PostMapping("/add-address")
    public ResponseEntity<ResponseWrapper<Void>> insertAddress(@Valid @RequestBody AddressInsertDTO addressInsertDTO,
                                                               HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);
        log.info("Request to add address for clientId: {}", clientId);

        Result<Void> addResult = addressService.addAddress(addressInsertDTO, clientId);
        if (!addResult.isSuccess()) {
            log.error("Failed to add address for clientId: {}. Error: {}", clientId, addResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Address", "ID"));
        }

        log.info("Address successfully created for clientId: {}", clientId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created("Address"));
    }

    @Operation(summary = "Update an existing address for the current client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Address not found",
                    content = @Content)
    })
    @PutMapping("/update-address")
    public ResponseEntity<ResponseWrapper<Void>> updateMyAddressOnMyList(@RequestBody AddressUpdateDTO addressUpdateDTO,
                                                                         HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);

        addressService.updateAddressFromClientList(addressUpdateDTO, clientId);

        return ResponseEntity.ok(ResponseWrapper.ok("Address", "Update"));
    }

    @Operation(summary = "Delete an address from the client's list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Address not found",
                    content = @Content)
    })
    @DeleteMapping("client/address/remove/{addressIndex}")
    public ResponseEntity<ResponseWrapper<Void>> deleteMyAddressOnMyList(@PathVariable int addressIndex,
                                                                         HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);

        addressService.deleteAddressFromClientList(addressIndex, clientId);

        return ResponseEntity.ok(ResponseWrapper.ok("Address", "Delete"));
    }

}
