package microservice.client_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.client_service.Service.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("v1/api/clients/address")
public class AddressController {

    private final AddressService addressService;

    @Autowired
    public AddressController(AddressService addressService) {
        this.addressService = addressService;
    }

    /**
     * Create a new address.
     * @param addressInsertDTO The DTO containing the new address information.
     * @param clientId     The ID of the client associated with the new address.
     * @param bindingResult    The result of the validation for the addressInsertDTO.
     * @return A containing a ResponseEntity with a success message or an error message.
     */
    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> insertAddress(@Valid @RequestBody AddressInsertDTO addressInsertDTO,
                                                              @RequestParam Long clientId,
                                                              BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            var validationError = ControllerValidation.handleValidationError(bindingResult);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false ,validationError, "Validation Error", 400));
        }

        Result<Void> addressResult  = addressService.addAddress(addressInsertDTO, clientId);
        if (!addressResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false ,null, addressResult.getErrorMessage(), 404));
        }

        return ResponseEntity.status(HttpStatus.CREATED).body( new ApiResponse<>(false,null, "Address Successfully Created!.", 200));
    }

    /**
     * Retrieve an address by its ID.
     *
     * @param addressId The ID of the address to retrieve.
     * @return A containing a ResponseEntity with the address information or an error message.
     */
    @GetMapping("{addressId}")
    public ResponseEntity<ApiResponse<AddressDTO>> getAddressById(@PathVariable Long addressId) {
        Result<AddressDTO> addressDTOResult = addressService.getAddressById(addressId);
        if (!addressDTOResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Address Not Found", 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(false, addressDTOResult.getData(), "Address Successfully Fetched", 200));
    }

    /**
     * Retrieve addresses associated with a client ID.
     * @param clientId The ID of the client.
     * @return A containing a ResponseEntity with the list of addresses or an error message.
     */
    @GetMapping("client/{clientId}")
    public ResponseEntity<ApiResponse<List<AddressDTO>>> getAddressesByClientId(@PathVariable Long clientId) {
         Result<List<AddressDTO>> addressesResult = addressService.getAddressesByClientId(clientId);
        if (!addressesResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, addressesResult.getErrorMessage(), 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, addressesResult.getData(), "Addresses Correctly Fetched", 200));
    }

    /**
     * Update an address by its ID.
     * @param addressInsertDTO The DTO containing the updated address information.
     * @param addressId The ID of the address to update.
     * @return A containing a ResponseEntity with a success message or an error message.
     */
    @PutMapping("client/address/update/{id}")
    public ResponseEntity<ApiResponse<Void>> updateAddressById(@RequestBody AddressInsertDTO addressInsertDTO,  @PathVariable Long addressId) {
       Result<Void> updateAddressResult = addressService.updateAddressById(addressInsertDTO, addressId);
        if (!updateAddressResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false ,null, updateAddressResult.getErrorMessage(), 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true ,null, "Address Successfully Updated.", 200));
    }

    /**
     * Delete an address by its ID.
     * @param addressId The ID of the address to delete.
     * @return A containing a ResponseEntity with a success message or an error message.
     */
    @DeleteMapping("client/address/remove/{addressId}")
    public ResponseEntity<ApiResponse<Void>> deleteAddressById(@PathVariable Long addressId) {
        Result<String> deleteResult = addressService.deleteAddressById(addressId);
        if (!deleteResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false,null, deleteResult.getErrorMessage(), 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, deleteResult.getErrorMessage(), 404));
    }

}
