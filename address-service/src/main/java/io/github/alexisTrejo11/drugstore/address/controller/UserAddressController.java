package io.github.alexisTrejo11.drugstore.address.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexisTrejo11.drugstore.address.controller.annotation.CreateUserAddressAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.DeleteUserAddressAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.GetAddressByIdAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.GetMyAddressesAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.SetDefaultAddressAnnotation;
import io.github.alexisTrejo11.drugstore.address.controller.annotation.UpdateUserAddressAnnotation;
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
import libs_kernel.security.UserAuthValidator;
import libs_kernel.security.dto.AuthUserDetails;

@RestController
@RequestMapping("/api/v2/user/addresses")
@Tag(name = "User Address Management", description = "Endpoints for users to manage their own addresses (requires USER or ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
public class UserAddressController {

  private final AddressService addressService;

  @Autowired
  public UserAddressController(AddressService addressService) {
    this.addressService = addressService;
  }

  @GetMapping
  @GetMyAddressesAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<List<AddressSummary>> getMyAddresses(
      @AuthenticationPrincipal AuthUserDetails userDetails) {
    UserAuthValidator.assertUserInContext(userDetails);

    List<AddressSummary> summaryResponses = addressService.findAddressSummariesByUserId(userDetails.getUserId());
    return ResponseWrapper.success(summaryResponses);
  }

  @GetMapping("/{addressId}")
  @GetAddressByIdAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<Address> getAddressById(
      @Parameter(description = "Address ID", required = true) @PathVariable String addressId,
      @AuthenticationPrincipal AuthUserDetails userDetails) {
    UserAuthValidator.assertUserInContext(userDetails);

    Address address = addressService.findAddressByIdAndUserId(addressId, userDetails.getUserId());
    return ResponseWrapper.success(address);
  }

  @PostMapping
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @CreateUserAddressAnnotation
  public ResponseEntity<ResponseWrapper<Address>> createAddress(
      @Parameter(description = "Address details", required = true) @Valid @RequestBody AddressRequest addressRequest,
      @AuthenticationPrincipal AuthUserDetails userDetails) {
    UserAuthValidator.assertUserInContext(userDetails);

    Address addressCreated = addressService.createAddress(
        userDetails.getUserId(),
        userDetails.getRole(),
        addressRequest);

    return ResponseEntity
        .status(HttpStatus.CREATED)
        .body(ResponseWrapper.created(addressCreated, "Address"));
  }

  @PutMapping("/{addressId}")
  @UpdateUserAddressAnnotation
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseWrapper<Address> updateAddress(
      @Parameter(description = "Address ID", required = true) @PathVariable String addressId,
      @Parameter(description = "Updated address details", required = true) @Valid @RequestBody AddressRequest addressRequest,
      @AuthenticationPrincipal AuthUserDetails userDetails) {
    UserAuthValidator.assertUserInContext(userDetails);

    Address response = addressService.updateAddress(addressId, addressRequest, userDetails.getUserId());

    return ResponseWrapper.success(response);
  }

  @DeleteMapping("/{addressId}")
  @DeleteUserAddressAnnotation
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  public ResponseWrapper<Void> deleteAddress(
      @Parameter(description = "Address ID", required = true) @PathVariable String addressId,
      @AuthenticationPrincipal AuthUserDetails userDetails) {

    UserAuthValidator.assertUserInContext(userDetails);

    addressService.deleteAddress(addressId, userDetails.getUserId());
    return ResponseWrapper.success("Address deleted successfully");
  }

  @PutMapping("/{addressId}/set-default")
  @SetDefaultAddressAnnotation
  @RateLimit(profile = RateLimitProfile.STANDARD)
  public ResponseWrapper<Address> setAddressAsDefault(
      @Parameter(description = "Address ID", required = true) @PathVariable String addressId,
      @AuthenticationPrincipal AuthUserDetails userDetails) {

    Address response = addressService.setAddressAsDefault(addressId, userDetails.getUserId());
    return ResponseWrapper.success(response);
  }
}