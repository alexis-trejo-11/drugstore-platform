package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.input.web.controller;

import io.github.alexisTrejo11.drugstore.payments.core.application.dto.response.SaleResponse;
import io.github.alexisTrejo11.drugstore.payments.core.port.input.PaymentApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponses;

import jakarta.validation.constraints.NotBlank;

import libs_kernel.response.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Sale queries.
 * <p>
 * Base path: /api/v1/sales
 * <p>
 * Sales are read-only from the API perspective — they are created automatically
 * when a Payment completes (via domain event). Mutations happen internally
 * through PaymentApplicationService when refunds are processed.
 * <p>
 * Security placeholder:
 * Add @PreAuthorize to restrict customer access to their own sales only.
 * Example: @PreAuthorize("@saleOwnerGuard.check(#saleId, authentication)")
 * <p>
 * Rate limiting placeholder:
 * Add @RateLimited(tier = "READ") on class or per method.
 */
@RestController
@RequestMapping("/api/v1/sales")
@Validated
@Tag(name = "Sales", description = "Sale records — automatically created from completed payments")
public class SaleController {

	private static final Logger logger = LoggerFactory.getLogger(SaleController.class);
	private final PaymentApplicationService paymentService;

	@Autowired
	public SaleController(PaymentApplicationService paymentService) {
		this.paymentService = paymentService;
	}


	@GetMapping("/{saleId}")
	@Operation(
			summary = "Get sale by ID",
			description = "Returns the sale record including net amount after refunds."
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Sale found"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Sale not found")
	})
	public ResponseEntity<ResponseWrapper<SaleResponse>> getSaleById(
			@Parameter(description = "Sale UUID", required = true)
			@PathVariable @NotBlank String saleId) {

		logger.debug("GET /api/v1/sales/{}", saleId);

		return ResponseEntity.ok(ResponseWrapper.success(paymentService.getSaleById(saleId)));
	}


	@GetMapping("/order/{orderId}")
	@Operation(
			summary = "Get sale by order ID",
			description = """
					Returns the sale linked to the given order.
					Useful for the Order microservice to verify a sale was confirmed
					before dispatching inventory or fulfillment events.
					"""
	)
	public ResponseEntity<ResponseWrapper<SaleResponse>> getSaleByOrderId(
			@Parameter(description = "Order UUID from the Order microservice", required = true)
			@PathVariable @NotBlank String orderId) {

		logger.debug("GET /api/v1/sales/order/{}", orderId);

		return ResponseEntity.ok(ResponseWrapper.success(paymentService.getSaleByOrderId(orderId)));
	}


	@GetMapping("/customer/{customerId}")
	@Operation(
			summary = "Get all sales by customer",
			description = """
					Returns the full sales history for a given customer.
					Includes confirmed, refunded and cancelled sales.
					
					Security note: once auth is wired, validate that the authenticated
					user matches the customerId path param or has ADMIN role.
					"""
	)
	public ResponseEntity<ResponseWrapper<List<SaleResponse>>> getSalesByCustomer(
			@Parameter(description = "Customer UUID", required = true)
			@PathVariable @NotBlank String customerId) {

		logger.debug("GET /api/v1/sales/customer/{}", customerId);

		return ResponseEntity.ok(ResponseWrapper.success(paymentService.getSalesByCustomerId(customerId)));
	}
}
