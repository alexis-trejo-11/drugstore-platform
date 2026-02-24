package io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentGateway;
import io.github.alexisTrejo11.drugstore.payments.core.domain.validation.DomainValidation;

/**
 * Holds the external gateway reference for tracking purposes.
 * For Stripe this will contain the PaymentIntent ID and optionally the charge
 * ID.
 * Gateway-agnostic by design — other providers can store their own reference
 * IDs.
 */
public record PaymentGatewayRef(
    PaymentGateway gateway,
    String gatewayPaymentId, // e.g. Stripe PaymentIntent ID: "pi_xxxxx"
    String gatewayChargeId // e.g. Stripe Charge ID: "ch_xxxxx" (nullable)
) {

  public static final PaymentGatewayRef NONE = new PaymentGatewayRef(PaymentGateway.NONE, "NONE", null);

  public PaymentGatewayRef {
    DomainValidation.requireNonNull(gateway, "PaymentGateway");
    DomainValidation.requireNonBlank(gatewayPaymentId, "gatewayPaymentId");
    // gatewayChargeId is optional, can be populated after charge confirmation
  }

  public static PaymentGatewayRef forStripe(String paymentIntentId) {
    return new PaymentGatewayRef(PaymentGateway.STRIPE, paymentIntentId, null);
  }

  public static PaymentGatewayRef forStripe(String paymentIntentId, String chargeId) {
    return new PaymentGatewayRef(PaymentGateway.STRIPE, paymentIntentId, chargeId);
  }

  public static PaymentGatewayRef manual() {
    return new PaymentGatewayRef(PaymentGateway.MANUAL, "MANUAL", null);
  }

  public boolean hasChargeId() {
    return gatewayChargeId != null && !gatewayChargeId.isBlank();
  }
}
