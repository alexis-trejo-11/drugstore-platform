package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.input.web.controller;

import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.StripeWebhookRequest;
import io.github.alexisTrejo11.drugstore.payments.core.port.input.PaymentApplicationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import libs_kernel.response.ResponseWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * Webhook endpoint for Stripe events.
 *
 * Base path: /api/v1/webhooks/stripe
 *
 * ─── Stripe Webhook Flow ───────────────────────────────────────────────────
 *
 *  Stripe Dashboard → POST /api/v1/webhooks/stripe
 *       │
 *       ├─ 1. Read raw body as String (must NOT be parsed first — needed for signature)
 *       ├─ 2. Verify Stripe-Signature header against whsec_ secret
 *       ├─ 3. Parse verified event
 *       ├─ 4. Map to StripeWebhookRequest
 *       └─ 5. Delegate to PaymentApplicationService.handleStripeWebhook()
 *
 * ─── Security ──────────────────────────────────────────────────────────────
 *
 *  This endpoint MUST be excluded from your Spring Security filter chain
 *  (no JWT required — Stripe signs its own requests).
 *
 *  In your SecurityFilterChain:
 *    .requestMatchers("/api/v1/webhooks/stripe").permitAll()
 *
 *  In your RateLimitFilter, you may want to whitelist Stripe IPs or
 *  apply a separate higher-limit bucket for this endpoint.
 *
 * ─── Handled Stripe Events ─────────────────────────────────────────────────
 *
 *  payment_intent.succeeded      → Payment COMPLETED → Sale CREATED
 *  payment_intent.payment_failed → Payment FAILED
 *  charge.refunded               → Payment REFUNDED → Sale updated
 *
 *  All other event types are acknowledged (200 OK) and ignored.
 *  Stripe requires a 2xx response within 30s or it will retry.
 *
 * ─── Idempotency ───────────────────────────────────────────────────────────
 *
 *  Stripe may send the same event multiple times (at-least-once delivery).
 *  Idempotency is handled at the domain layer:
 *  - PaymentDomainService checks payment.isCompleted() before confirming
 *  - SaleDomainService checks existsByPaymentId() before creating Sale
 */
@RestController
@RequestMapping("/api/v1/webhooks")
@Tag(name = "Webhooks", description = "Stripe webhook receiver — do not call manually")
public class StripeWebhookController {

    private static final Logger logger = LoggerFactory.getLogger(StripeWebhookController.class);

    // Stripe event types we handle
    private static final String PAYMENT_INTENT_SUCCEEDED = "payment_intent.succeeded";
    private static final String PAYMENT_INTENT_FAILED    = "payment_intent.payment_failed";
    private static final String CHARGE_REFUNDED          = "charge.refunded";

    private final PaymentApplicationService paymentService;
    private final StripeWebhookVerifier webhookVerifier;

		@Autowired
    public StripeWebhookController(PaymentApplicationService paymentService,
                                    StripeWebhookVerifier webhookVerifier) {
        this.paymentService = paymentService;
        this.webhookVerifier = webhookVerifier;
    }

    @PostMapping("/stripe")
    @Operation(
        summary = "Stripe webhook receiver",
        description = """
            Receives and processes Stripe webhook events.
            Verifies the Stripe-Signature header before processing.
            Returns 200 immediately to acknowledge receipt — processing is synchronous.
            Register this URL in your Stripe Dashboard → Developers → Webhooks.
            """
    )
    public ResponseEntity<ResponseWrapper<Void>> handleStripeWebhook(
            @RequestBody String rawPayload,
            @RequestHeader("Stripe-Signature") String stripeSignature) {

        logger.info("Stripe webhook received | signaturePresent={}", stripeSignature != null);

        // Step 1: Verify signature and parse event
        StripeWebhookRequest webhookRequest = webhookVerifier.verifyAndParse(rawPayload, stripeSignature);

        logger.info("Stripe webhook verified | eventType={} paymentIntentId={}",
                webhookRequest.eventType(), webhookRequest.paymentIntentId());

        // Step 2: Delegate to application service
        // Only process known event types — ignore others but still return 200
        if (webhookRequest.isSucceeded() || webhookRequest.isFailed() || webhookRequest.isRefunded()) {
            paymentService.handleStripeWebhook(webhookRequest);
        } else {
            logger.debug("Unhandled Stripe event type: {} — acknowledging without processing",
                    webhookRequest.eventType());
        }

        // Step 3: Always return 200 so Stripe doesn't retry
        return ResponseEntity.ok(ResponseWrapper.success());
    }
}