package io.github.alexisTrejo11.drugstore.payments.core.application.dto.response;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Payment;

public record PaymentResponse() {

	public static PaymentResponse from(Payment payment) {
		return new PaymentResponse();
	}


	public static PaymentResponse from(Payment payment, String clientSecret) {
		return new PaymentResponse();
	}
}
