package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output;

import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.port.StripeGatewayPort;
import org.springframework.stereotype.Service;

@Service
public class StripeGatewayAdapter implements StripeGatewayPort {
	@Override
	public StripePaymentIntentResult createPaymentIntent(Money amount, String idempotencyKey) {
		return null;
	}

	@Override
	public String refundCharge(String gatewayChargeId, String reason) {
		return "";
	}

	@Override
	public String partialRefundCharge(String gatewayChargeId, Money amount, String reason) {
		return "";
	}
}
