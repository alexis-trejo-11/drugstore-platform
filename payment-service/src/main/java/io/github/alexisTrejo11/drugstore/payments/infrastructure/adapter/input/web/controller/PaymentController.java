package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.input.web.controller;


import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.InitiatePaymentRequest;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.RefundRequest;
import io.github.alexisTrejo11.drugstore.payments.core.port.input.PaymentApplicationService;
import io.github.alexisTrejo11.drugstore.payments.core.application.dto.response.PaymentResponse;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;

import libs_kernel.response.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for Payment operations.
 * <p>
 * Base path: /api/v1/payments
 * <p>
 * Security placeholder:
 * Add @PreAuthorize("hasRole('CUSTOMER')") or JWT extraction once
 * your Spring Security filter chain is wired.
 * <p>
 * Rate limiting placeholder:
 * Your rate limit filter applies before this controller.
 * Annotate with @RateLimited (your custom annotation) if you need
 * per-endpoint granularity.
 */
@RestController
@RequestMapping("/api/v1/payments")
@Validated
@Tag(name = "Payments", description = "Payment lifecycle management — initiate, track and refund payments")
public class PaymentController {

	private static final Logger logger = LoggerFactory.getLogger(PaymentController.class);
	private final PaymentApplicationService paymentService;

	@Autowired
	public PaymentController(PaymentApplicationService paymentService) {
		this.paymentService = paymentService;
	}


	@PostMapping
	@Operation(
			summary = "Initiate a payment",
			description = """
					Creates a payment in PENDING state, registers a Stripe PaymentIntent,
					transitions to PROCESSING and returns the clientSecret needed by the
					frontend to call stripe.confirmPayment().
					
					Flow:
					  1. POST /api/v1/payments  → receive clientSecret
					  2. Frontend calls stripe.confirmPayment(clientSecret)
					  3. Stripe calls POST /api/v1/webhooks/stripe on success/failure
					"""
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201",
					description = "Payment initiated successfully. Contains clientSecret for Stripe.js"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400",
					description = "Invalid request body",
					content = @Content(schema = @Schema(implementation = ResponseWrapper.class))),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422",
					description = "Business rule violation (e.g. duplicate payment for order)",
					content = @Content(schema = @Schema(implementation = ResponseWrapper.class)))
	})
	public ResponseEntity<ResponseWrapper<PaymentResponse>> initiatePayment(
			@Valid @RequestBody InitiatePaymentRequest request) {

		logger.info("POST /api/v1/payments | orderId={} customerId={} amount={} {}",
				request.orderId(), request.customerId(), request.amount(), request.currency());

		PaymentResponse response = paymentService.initiatePayment(request);

		return ResponseEntity
				.status(HttpStatus.CREATED)
				.body(ResponseWrapper.created(response));
	}


	@PostMapping("/refund")
	@Operation(
			summary = "Process a refund",
			description = """
					Processes a full or partial refund for a completed payment.
					- If `amount` is null → full refund
					- If `amount` is provided → partial refund for that amount
					
					Calls Stripe to issue the refund, then updates Payment and Sale status.
					"""
	)
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200",
					description = "Refund processed successfully"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422",
					description = "Payment is not refundable or amount exceeds refundable balance")
	})
	public ResponseEntity<ResponseWrapper<PaymentResponse>> processRefund(
			@Valid @RequestBody RefundRequest request) {

		logger.info("POST /api/v1/payments/refund | paymentId={} isPartial={} amount={}",
				request.paymentId(), request.isPartial(), request.amount());

		PaymentResponse response = paymentService.processRefund(request);

		return ResponseEntity.ok(ResponseWrapper.success(response));
	}


	@GetMapping("/{paymentId}")
	@Operation(summary = "Get payment by ID")
	@ApiResponses({
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Payment found"),
			@io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "422", description = "Payment not found")
	})
	public ResponseEntity<ResponseWrapper<PaymentResponse>> getPaymentById(
			@Parameter(description = "Payment UUID", required = true)
			@PathVariable @NotBlank String paymentId) {

		logger.debug("GET /api/v1/payments/{}", paymentId);

		return ResponseEntity.ok(ResponseWrapper.success(paymentService.getPaymentById(paymentId)));
	}

	// ─── GET /api/v1/payments/order/{orderId} ─────────────────────────────────

	@GetMapping("/order/{orderId}")
	@Operation(
			summary = "Get payment by order ID",
			description = "Returns the payment associated with the given order. One-to-one relationship."
	)
	public ResponseEntity<ResponseWrapper<PaymentResponse>> getPaymentByOrderId(
			@Parameter(description = "Order UUID from the Order microservice", required = true)
			@PathVariable @NotBlank String orderId) {
		logger.debug("GET /api/v1/payments/order/{}", orderId);
		PaymentResponse paymentResponse = paymentService.getPaymentByOrderId(orderId);
		return ResponseEntity.ok(ResponseWrapper.success(paymentResponse));
	}


	@GetMapping("/customer/{customerId}")
	@Operation(
			summary = "Get all payments by customer",
			description = "Returns all payments for a given customer, ordered by creation date descending."
	)
	public ResponseWrapper<List<PaymentResponse>> getPaymentsByCustomer(
			@Parameter(description = "Customer UUID", required = true)
			@PathVariable @NotBlank String customerId) {

		logger.debug("GET /api/v1/payments/customer/{}", customerId);
		List<PaymentResponse> paymentResponses = paymentService.getPaymentsByCustomerId(customerId);
		return ResponseWrapper.success(paymentResponses);
	}
}