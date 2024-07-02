package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.client_service.Service.AddressService;
import microservice.client_service.Utils.ControllerHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Create a new address.
     *
     * @param addressInsertDTO The DTO containing the new address information.
     * @param clientId     The ID of the client associated with the new address.
     * @param bindingResult    The result of the validation for the addressInsertDTO.
     * @return A CompletableFuture containing a ResponseEntity with a success message or an error message.
     */
    @PostMapping("client/address/add")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> insertAddress(@Valid @RequestBody AddressInsertDTO addressInsertDTO,
                                                              @RequestParam(required = true) String clientId,
                                                              BindingResult bindingResult) {

        // Validate and parse the clientIdStr into a Long using ControllerHelper.validateAndParseId method
        Result<Long> clientIdResult = ControllerHelper.validateAndParseId(clientId, Long.class);
        // Check if the validation and parsing were successful
        if (!clientIdResult.isSuccess()) {
            // If validation/parsing failed, return a BAD_REQUEST response with the error message
            Map<String, String> errors = new HashMap<>();
            errors.put("clientId", clientIdResult.getErrorMessage());
            ResponseWrapper validationErrorResponse = new ResponseWrapper<>(errors, "validation error");
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(validationErrorResponse)
            );
        }
        // Get the parsed client ID
        Long clientIdParsed = clientIdResult.getData();

        // Perform the address insertion asynchronously
        return addressService.addAddress(addressInsertDTO, clientIdParsed)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, "Address Successfully Created!.", HttpStatus.CREATED);
                        return ResponseEntity.status(HttpStatus.CREATED).body(response);
                    }
                }).exceptionally(ex -> {
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    /**
     * Retrieve an address by its ID.
     *
     * @param id The ID of the address to retrieve.
     * @return A CompletableFuture containing a ResponseEntity with the address information or an error message.
     */
    @GetMapping("client/address/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<AddressDTO>>> getAddressById(@PathVariable Long id) {
        return addressService.getAddressById(id)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<AddressDTO> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage() );
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<AddressDTO> response = new ResponseWrapper<>(result.getData(),null);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                        }
                }).exceptionally(ex -> {
                        ResponseWrapper<AddressDTO> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                    });
    }

    /**
     * Retrieve addresses associated with a client ID.
     *
     * @param clientId The ID of the client.
     * @return A CompletableFuture containing a ResponseEntity with the list of addresses or an error message.
     */
    @GetMapping("client/address-client/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<AddressDTO>>>> getAddressesByClientId(@PathVariable Long clientId) {
        return addressService.getAddressesByClientId(clientId)
                .thenApplyAsync(listResult -> {
                    if (!listResult.isSuccess()) {
                        ResponseWrapper<List<AddressDTO>> errorResponse = new ResponseWrapper<>(null, listResult.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<List<AddressDTO>> response = new ResponseWrapper<>(listResult.getData(), null);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }).exceptionally(ex -> {
                    ResponseWrapper<List<AddressDTO>> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    /**
     * Update an address by its ID.
     *
     * @param addressInsertDTO The DTO containing the updated address information.
     * @param id               The ID of the address to update.
     * @return A CompletableFuture containing a ResponseEntity with a success message or an error message.
     */
    @PutMapping("client/address/update/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> updateAddressById(@RequestBody AddressInsertDTO addressInsertDTO,  @PathVariable Long id) {
        return addressService.updateAddressById(addressInsertDTO, id)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }).exceptionally(ex -> {
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    /**
     * Delete an address by its ID.
     *
     * @param id The ID of the address to delete.
     * @return A CompletableFuture containing a ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("client/address/remove/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteAddressById(@PathVariable Long id) {
        return addressService.deleteAddressById(id)
                .thenApplyAsync(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(null, null, HttpStatus.OK);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                }).exceptionally(ex -> {
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

}
