package io.github.alexisTrejo11.drugstore.payments.core.port;

import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentGatewayRef;

/**
 * Application-layer port for Stripe (and future gateways).
 * The infrastructure adapter implements this using the Stripe Java SDK.
 *
 * Keeps the application service completely unaware of Stripe internals.
 */
public interface StripeGatewayPort {

  /**
   * Creates a Stripe PaymentIntent and returns the gateway reference.
   * This does NOT charge the customer — it only registers intent.
   *
   * @param amount         Amount and currency to charge
   * @param idempotencyKey Unique key to avoid duplicate PaymentIntents (use
   *                       orderId)
   * @return PaymentGatewayRef containing the Stripe PaymentIntentId and client
   *         secret
   */
  StripePaymentIntentResult createPaymentIntent(Money amount, String idempotencyKey);

  /**
   * Issues a full refund on Stripe for the given charge.
   *
   * @param gatewayChargeId Stripe Charge ID (ch_xxxxx)
   * @param reason          Human-readable reason for Stripe records
   * @return Stripe Refund ID (re_xxxxx)
   */
  String refundCharge(String gatewayChargeId, String reason);

  /**
   * Issues a partial refund on Stripe.
   *
   * @param gatewayChargeId Stripe Charge ID
   * @param amount          Amount to refund
   * @param reason          Reason for refund
   * @return Stripe Refund ID
   */
  String partialRefundCharge(String gatewayChargeId, Money amount, String reason);

  /**
   * Result wrapper for Stripe PaymentIntent creation.
   * Contains all the data needed to proceed with the payment flow.
   */
  record StripePaymentIntentResult(
      String paymentIntentId, // pi_xxxxx — used to track on our side
      String clientSecret // Returned to frontend to confirm payment
  ) {
    public PaymentGatewayRef toGatewayRef() {
      return PaymentGatewayRef.forStripe(paymentIntentId);
    }
  }
}