package microservice.adress_service.Controller;

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
import microservice.adress_service.Service.ClientAddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

@Slf4j
@RestController
@RequestMapping("v1/drugstore/client-address")
public class ClientAddressController {

    private final ClientAddressService clientAddressService;
    private final AuthSecurity authSecurity;

    @Autowired
    public ClientAddressController(ClientAddressService clientAddressService,
                                   AuthSecurity authSecurity) {
        this.clientAddressService = clientAddressService;
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
    @GetMapping("/my-addresses")
    public ResponseEntity<ResponseWrapper<List<AddressDTO>>> getMyAddresses(HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);

        CompletableFuture<List<AddressDTO>> addressDTOsAsync = clientAddressService.getAddressesByClientId(clientId);
        List<AddressDTO> addressDTOs = addressDTOsAsync.join();

        return ResponseEntity.ok(ResponseWrapper.found(addressDTOs, "Addresses"));
    }

    @Operation(summary = "Insert a new address for the current client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Address created",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Failed to create address",
                    content = @Content)
    })
    @PostMapping("/")
    public ResponseEntity<ResponseWrapper<Void>> insertAddress(@Valid @RequestBody AddressInsertDTO addressInsertDTO,
                                                               HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);

        Result<Void> addResult = clientAddressService.addAddress(addressInsertDTO, clientId);
        if (!addResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Address"));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created("Address"));
    }

    @Operation(summary = "Update an existing address for the current client")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address updated",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Address not found",
                    content = @Content)
    })
    @PutMapping("/")
    public ResponseEntity<ResponseWrapper<Void>> updateMyAddressOnMyList(@RequestBody AddressUpdateDTO addressUpdateDTO,
                                                                         HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);

        clientAddressService.updateAddressFromClient(addressUpdateDTO, clientId);
        return ResponseEntity.ok(ResponseWrapper.ok("Address", "Update"));
    }

    @Operation(summary = "Delete an address from the client's list")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Address deleted",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "Address not found",
                    content = @Content)
    })
    @DeleteMapping("/{addresId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteMyAddressOnMyList(@PathVariable Long addresId,
                                                                         HttpServletRequest request) {
        Long clientId = authSecurity.getClientIdFromToken(request);

        clientAddressService.deleteAddressFromClient(addresId, clientId);
        return ResponseEntity.ok(ResponseWrapper.ok("Address", "Delete"));
    }

}
