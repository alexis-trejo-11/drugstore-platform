package io.github.alexisTrejo11.drugstore.payments.core.application.dto.request;

public record StripeWebhookRequest(
		String paymentIntentId,
		String eventType,
		String chargeId,
		String failureReason
) {

	public boolean isSucceeded() {
		return "payment_intent.succeeded".equals(eventType);
	}

	public boolean isFailed() {
		return "payment_intent.payment_failed".equals(eventType);
	}

	public boolean isRefunded() {
		return "charge.refunded".equals(eventType);
	}
}
