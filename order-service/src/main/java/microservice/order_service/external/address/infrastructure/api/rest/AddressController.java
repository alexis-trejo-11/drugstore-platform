package microservice.order_service.external.address.infrastructure.api.rest;

import jakarta.validation.Valid;
import libs_kernel.mapper.DomainMapper;
import libs_kernel.mapper.ResponseMapper;
import libs_kernel.mapper.ResultMapper;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;
import microservice.order_service.external.address.domain.model.DeliveryAddress;
import microservice.order_service.external.address.domain.ports.input.AddressService;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressRequest;
import microservice.order_service.external.address.infrastructure.api.dto.DeliveryAddressResponse;
import microservice.order_service.orders.domain.models.valueobjects.AddressID;
import microservice.order_service.orders.domain.models.valueobjects.OrderID;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v2/users/addresses")
public class AddressController {
    private final AddressService addressService;
    private final ResponseMapper<DeliveryAddressResponse, DeliveryAddress> mapper;

    @GetMapping("/{id}")
    public ResponseWrapper<DeliveryAddressResponse> getAddressByID(@PathVariable String id) {
        var addressID = AddressID.of(id);
        var address = addressService.getAddressByID(addressID);

        var addressResponse = mapper.toResponse(address);
        return ResponseWrapper.found(addressResponse, "Address");
    }

    @GetMapping("/user/{userId}")
    public ResponseWrapper<List<DeliveryAddressResponse>> getAddressesByUserID(@PathVariable String userId) {
        var userID = UserID.of(userId);
        var queryResult = addressService.getAddressesByUserID(userID);

        var addressResponses = mapper.toResponses(queryResult);
        return ResponseWrapper.found(addressResponses, "Addresses");
    }

    @GetMapping("/default/user/{id}")
    public ResponseWrapper<DeliveryAddressResponse> getDefaultAddressByUserID(@PathVariable String id) {
        var userID = UserID.of(id);
        var queryResult = addressService.getDefaultAddressByUserID(userID);

        var addressResponse = mapper.toResponse(queryResult);
        return ResponseWrapper.found(addressResponse, "Default Address");
    }


    @GetMapping("/orders/{id}")
    public ResponseWrapper<DeliveryAddressResponse> getAddressByOrderID(@PathVariable String id) {
        var orderID = OrderID.of(id);
        var address = addressService.getAddressByOrderID(orderID);

        var addressResponse = mapper.toResponse(address);
        return ResponseWrapper.found(addressResponse, "Address");
    }

    public ResponseEntity<ResponseWrapper<AddressID>> createAddress(@Valid @RequestBody DeliveryAddressRequest request) {
        var deliveryAddress= request.toDomain();

        var addressID = addressService.createAddress(deliveryAddress);

        return ResponseEntity.status(201).body(ResponseWrapper.created(addressID, "Address"));
    }

    @PutMapping("/{id}")
    public ResponseWrapper<Void> updateAddress(
            @Valid @PathVariable String id,
            @RequestBody DeliveryAddressRequest request
    ) {
        var addressID = AddressID.of(id);
        var address = request.toDomainWithID(addressID);

        addressService.updateAddress(address);
        return ResponseWrapper.success("Address successfully updated");
    }

    @PatchMapping("/{id}/set-default/user/{userId}")
    public ResponseWrapper<Void> setDefaultAddress(@PathVariable String id, @PathVariable String userId) {
        var userID = UserID.of(userId);
        var addressID = AddressID.of(id);

        addressService.setDefaultAddress(userID, addressID);
        return ResponseWrapper.success("Address set as default successfully");
    }


}
