package microservice.order_service.external.address.infrastructure.api.rest;

import jakarta.validation.Valid;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.domain.ports.input.AddressService;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressRequest;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.external.address.infrastructure.api.dto.UpdateAddressRequest;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2")
public class AddressController {
    private final AddressService addressService;
    private final ResponseMapper<DeliveryAddressResponse, DeliveryAddress> mapper;

    @GetMapping("/addresses/{id}")
    public ResponseWrapper<DeliveryAddressResponse> getAddressByID(@PathVariable String id) {
        var addressID = AddressID.of(id);
        var address = addressService.getAddressByID(addressID);

        var addressResponse = mapper.toResponse(address);
        return ResponseWrapper.found(addressResponse, "Address");
    }

    @GetMapping("/orders/addresses/{id}")
    public ResponseWrapper<DeliveryAddressResponse> getAddressByOrderID(@PathVariable String id) {
        var orderID = OrderID.of(id);
        var address = addressService.getAddressByOrderID(orderID);

        var addressResponse = mapper.toResponse(address);
        return ResponseWrapper.found(addressResponse, "Address");
    }

    @GetMapping("/users/{userId}/addresses")
    public ResponseWrapper<List<DeliveryAddressResponse>> getAddressesByUserID(@PathVariable String userId) {
        var userID = UserID.of(userId);
        var queryResult = addressService.getAddressesByUserID(userID);

        var addressResponses = mapper.toResponses(queryResult);
        return ResponseWrapper.found(addressResponses, "Addresses");
    }

    @GetMapping("/users/{id}/addresses/default")
    public ResponseWrapper<DeliveryAddressResponse> getDefaultAddressByUserID(@PathVariable String id) {
        var userID = UserID.of(id);
        var queryResult = addressService.getDefaultAddressByUserID(userID);

        var addressResponse = mapper.toResponse(queryResult);
        return ResponseWrapper.found(addressResponse, "Default Address");
    }


    @PostMapping("/addresses")
    public ResponseEntity<ResponseWrapper<AddressID>> createAddress(@Valid @RequestBody DeliveryAddressRequest request) {
        var deliveryAddress= request.toDomain();
        var addressID = addressService.createAddress(deliveryAddress);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(addressID, "Address"));
    }

    @PutMapping("/addresses/{id}")
    public ResponseWrapper<Void> updateAddress(
            @Valid @PathVariable String id,
            @RequestBody UpdateAddressRequest request
    ) {
        var addressID = AddressID.of(id);
        var address = request.toDomain(addressID);

        addressService.updateAddress(address);
        return ResponseWrapper.success("Address successfully updated");
    }

    @PatchMapping("/users/{userId}/addresses/{id}/set-default")
    public ResponseWrapper<Void> setDefaultAddress(@PathVariable String id, @PathVariable String userId) {
        var userID = UserID.of(userId);
        var addressID = AddressID.of(id);

        addressService.setDefaultAddress(userID, addressID);
        return ResponseWrapper.success("Address set as default successfully");
    }

    @DeleteMapping("/addresses/{id}")
    public ResponseWrapper<Void> deleteAddress(@PathVariable String id) {
        var addressID = AddressID.of(id);
        addressService.deleteAddress(addressID);
        return ResponseWrapper.success("Address successfully deleted");
    }
}
