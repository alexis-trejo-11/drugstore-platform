package io.github.alexisTrejo11.drugstore.address.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexisTrejo11.drugstore.address.controller.annotation.CreateAddressForUserAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.DeleteAddressAdminAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.GetAddressByIdAdminAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.GetAddressesByUserIdAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.GetAllAddressesAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.SetDefaultAddressAdminAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.UpdateAddressAdminAnnotation;
import io.github.alexisTrejo11.drugstore.address.service.AddressService;
import io.github.alexisTrejo11.drugstore.address.utils.dto.Address;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressRequest;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressSummary;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v2/addresses/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Address Management", description = "Endpoints for administrative address management (requires ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
public class AddressAdminController {

  private final AddressService addressService;

  @GetMapping
  @GetAllAddressesAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<Page<AddressSummary>> getAllAddresses(
      @Parameter(description = "Pagination parameters") @PageableDefault(size = 20) Pageable pageable) {
    Page<AddressSummary> addresses = addressService.findAllAddresses(pageable);
    return ResponseWrapper.found(addresses, "Addresses");
  }

  @GetMapping("/{id}")
  @GetAddressByIdAdminAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<Address> getAddressById(
      @Parameter(description = "Address ID", required = true) @PathVariable String id) {
    Address address = addressService.findAddressById(id);
    return ResponseWrapper.found(address, "Address");
  }

  @GetMapping("/user/{userId}")
  @GetAddressesByUserIdAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<List<Address>> getAddressesByUserId(
      @Parameter(description = "User ID", required = true) @PathVariable String userId) {
    List<Address> addresses = addressService.findAddressesByUserId(userId);
    return ResponseWrapper.found(addresses, "Addresses");
  }

  @PostMapping
  @CreateAddressForUserAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseEntity<ResponseWrapper<Address>> createAddressForUser(
      @Parameter(description = "User ID to assign the address to", required = true) @RequestParam String userId,
      @Parameter(description = "Address details", required = true) @Valid @RequestBody AddressRequest addressRequest) {

    Address address = addressService.createAddress(userId, "ADMIN", addressRequest);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResponseWrapper.created(address, "Address"));
  }

  @PutMapping("/{id}")
  @UpdateAddressAdminAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<Address> updateAddress(
      @Parameter(description = "Address ID", required = true) @PathVariable String id,
      @Parameter(description = "Updated address details", required = true) @Valid @RequestBody AddressRequest addressRequest) {

    Address address = addressService.updateAddress(id, addressRequest);
    return ResponseWrapper.updated(address, "Address");
  }

  @DeleteMapping("/{id}")
  @DeleteAddressAdminAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<Void> deleteAddress(
      @Parameter(description = "Address ID", required = true) @PathVariable String id) {

    addressService.deleteAddress(id);
    return ResponseWrapper.success("Address");
  }

  @PutMapping("/{id}/set-default-for-user/{userId}")
  @SetDefaultAddressAdminAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<Address> setAddressAsDefault(
      @Parameter(description = "Address ID", required = true) @PathVariable String id,
      @Parameter(description = "User ID", required = true) @PathVariable String userId) {

    Address address = addressService.setAddressAsDefault(id, userId);
    return ResponseWrapper.updated(address, "Address");
  }
}