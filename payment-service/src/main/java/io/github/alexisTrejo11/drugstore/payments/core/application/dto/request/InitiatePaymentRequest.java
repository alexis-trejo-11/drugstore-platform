package io.github.alexisTrejo11.drugstore.payments.core.application.dto.request;

import java.math.BigDecimal;

public record InitiatePaymentRequest(
		String orderId,
		String customerId,
		String currency,
		String paymentMethod,
		BigDecimal amount) {
}
