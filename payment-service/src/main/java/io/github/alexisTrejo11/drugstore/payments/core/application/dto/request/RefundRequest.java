package io.github.alexisTrejo11.drugstore.payments.core.application.dto.request;

import java.math.BigDecimal;

public record RefundRequest(
		String paymentId,
		String orderId,
		String customerId,
		boolean isPartial,
		BigDecimal amount,
		String reason
) {
}
