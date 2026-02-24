package io.github.alexisTrejo11.drugstore.address.controller;

import io.github.alexisTrejo11.drugstore.address.service.AddressService;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressRequest;
import io.github.alexisTrejo11.drugstore.address.utils.dto.Address;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressSummary;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import libs_kernel.security.UserAuthValidator;
import libs_kernel.security.dto.AuthUserDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
	@Operation(summary = "Get my addresses", description = "Retrieves all addresses for the authenticated user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved addresses"),
			@ApiResponse(responseCode = "401", description = "Unauthorized - Authentication required")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseWrapper<List<AddressSummary>> getMyAddresses(
			@AuthenticationPrincipal AuthUserDetails userDetails) {
		UserAuthValidator.assertUserInContext(userDetails);

		List<AddressSummary> summaryResponses = addressService.findAddressSummariesByUserId(userDetails.getUserId());
		return ResponseWrapper.success(summaryResponses);
	}

	@GetMapping("/{addressId}")
	@Operation(summary = "Get address by ID", description = "Retrieves a specific address for the authenticated user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved address"),
			@ApiResponse(responseCode = "403", description = "Access denied - Address belongs to another user"),
			@ApiResponse(responseCode = "404", description = "Address not found")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseWrapper<Address> getAddressById(
			@Parameter(description = "Address ID", required = true)
			@PathVariable String addressId,
			@AuthenticationPrincipal AuthUserDetails userDetails) {

		UserAuthValidator.assertUserInContext(userDetails);

		return ResponseWrapper.success(addressService.findAddressByIdAndUserId(addressId, userDetails.getUserId()));
	}

	@PostMapping
	@Operation(summary = "Create new address", description = "Creates a new address for the authenticated user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Address created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input data")
	})
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<Address>> createAddress(
			@Parameter(description = "Address details", required = true)
			@Valid @RequestBody AddressRequest addressRequest,
			@AuthenticationPrincipal AuthUserDetails userDetails) {
		UserAuthValidator.assertUserInContext(userDetails);

		Address addressCreated = addressService.createAddress(userDetails.getUserId(), addressRequest);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(ResponseWrapper.created(addressCreated, "Address"));
	}

	@PutMapping("/{addressId}")
	@Operation(summary = "Update address", description = "Updates an existing address for the authenticated user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Address updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "403", description = "Access denied - Address belongs to another user"),
			@ApiResponse(responseCode = "404", description = "Address not found")
	})
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<Address> updateAddress(
			@Parameter(description = "Address ID", required = true)
			@PathVariable String addressId,
			@Parameter(description = "Updated address details", required = true)
			@Valid @RequestBody AddressRequest addressRequest,
			@AuthenticationPrincipal AuthUserDetails userDetails) {
		UserAuthValidator.assertUserInContext(userDetails);

		Address response = addressService.updateAddress(addressId, addressRequest, userDetails.getUserId());

		return ResponseWrapper.success(response);
	}

	@DeleteMapping("/{addressId}")
	@Operation(summary = "Delete address", description = "Deletes an address for the authenticated user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Address deleted successfully"),
			@ApiResponse(responseCode = "403", description = "Access denied - Address belongs to another user"),
			@ApiResponse(responseCode = "404", description = "Address not found")
	})
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseWrapper<Void> deleteAddress(
			@Parameter(description = "Address ID", required = true)
			@PathVariable String addressId,
			@AuthenticationPrincipal AuthUserDetails userDetails) {

		UserAuthValidator.assertUserInContext(userDetails);

		addressService.deleteAddress(addressId, userDetails.getUserId());
		return ResponseWrapper.success("Address deleted successfully");
	}

	@PutMapping("/{addressId}/set-default")
	@Operation(summary = "Set address as default", description = "Sets a specific address as default for the authenticated user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Address set as default successfully"),
			@ApiResponse(responseCode = "403", description = "Access denied - Address belongs to another user"),
			@ApiResponse(responseCode = "404", description = "Address not found")
	})
	public ResponseWrapper<Address> setAddressAsDefault(
			@Parameter(description = "Address ID", required = true)
			@PathVariable String addressId,
			@AuthenticationPrincipal UserDetails userDetails) {

		Address response = addressService.setAddressAsDefault(addressId, "userId");
		return ResponseWrapper.success(response);
	}
}