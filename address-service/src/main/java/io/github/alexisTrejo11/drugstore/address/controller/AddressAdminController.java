package io.github.alexisTrejo11.drugstore.address.controller;

import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressRequest;
import io.github.alexisTrejo11.drugstore.address.utils.dto.Address;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressSummary;
import io.github.alexisTrejo11.drugstore.address.service.AddressService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
// import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
	@RequestMapping("/api/v2/addresses/admin")
@RequiredArgsConstructor
@Tag(name = "Admin Address Management", description = "Endpoints for administrative address management (requires ADMIN role)")
@SecurityRequirement(name = "bearerAuth")
public class AddressAdminController {

	private final AddressService addressService;

	@GetMapping
	@Operation(summary = "Get all addresses with pagination", description = "Retrieves a paginated list of all addresses in the system")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved addresses"),
			@ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseEntity<Page<AddressSummary>> getAllAddresses(
			@Parameter(description = "Pagination parameters")
			@PageableDefault(size = 20) Pageable pageable) {
		return ResponseEntity.ok(addressService.findAllAddresses(pageable));
	}

	@GetMapping("/{id}")
	@Operation(summary = "Get address by ID", description = "Retrieves detailed address information by its ID")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved address"),
			@ApiResponse(responseCode = "404", description = "Address not found"),
			@ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseEntity<Address> getAddressById(
			@Parameter(description = "Address ID", required = true)
			@PathVariable String id) {
		return ResponseEntity.ok(addressService.findAddressById(id));
	}

	@GetMapping("/user/{userId}")
	@Operation(summary = "Get addresses by user ID", description = "Retrieves all addresses for a specific user")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Successfully retrieved user addresses"),
			@ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseEntity<List<Address>> getAddressesByUserId(
			@Parameter(description = "User ID", required = true)
			@PathVariable String userId) {
		return ResponseEntity.ok(addressService.findAddressesByUserId(userId));
	}

	@PostMapping
	@Operation(summary = "Create address for any user", description = "Creates a new address for any user (admin only)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "201", description = "Address created successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseEntity<Address> createAddressForUser(
			@Parameter(description = "User ID to assign the address to", required = true)
			@RequestParam String userId,

			@Parameter(description = "Address details", required = true)
			@Valid @RequestBody AddressRequest addressRequest) {
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(addressService.createAddress(userId, addressRequest));
	}

	@PutMapping("/{id}")
	@Operation(summary = "Update any address", description = "Updates an existing address by ID (admin only)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Address updated successfully"),
			@ApiResponse(responseCode = "400", description = "Invalid input data"),
			@ApiResponse(responseCode = "404", description = "Address not found"),
			@ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseEntity<Address> updateAddress(
			@Parameter(description = "Address ID", required = true)
			@PathVariable String id,

			@Parameter(description = "Updated address details", required = true)
			@Valid @RequestBody AddressRequest addressRequest) {
		return ResponseEntity.ok(addressService.updateAddress(id, addressRequest));
	}

	@DeleteMapping("/{id}")
	@Operation(summary = "Delete any address", description = "Deletes an address by ID (admin only)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "204", description = "Address deleted successfully"),
			@ApiResponse(responseCode = "404", description = "Address not found"),
			@ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseEntity<Void> deleteAddress(
			@Parameter(description = "Address ID", required = true)
			@PathVariable String id) {
		addressService.deleteAddress(id);
		return ResponseEntity.noContent().build();
	}

	@PutMapping("/{id}/set-default-for-user/{userId}")
	@Operation(summary = "Set address as default for a user", description = "Sets a specific address as default for a user (admin only)")
	@ApiResponses(value = {
			@ApiResponse(responseCode = "200", description = "Address set as default successfully"),
			@ApiResponse(responseCode = "404", description = "Address or user not found"),
			@ApiResponse(responseCode = "403", description = "Access denied - ADMIN role required")
	})
	@RateLimit(profile = RateLimitProfile.STANDARD)
	public ResponseEntity<Address> setAddressAsDefault(
			@Parameter(description = "Address ID", required = true)
			@PathVariable String id,

			@Parameter(description = "User ID", required = true)
			@PathVariable String userId) {
		return ResponseEntity.ok(addressService.setAddressAsDefault(id, userId));
	}
}