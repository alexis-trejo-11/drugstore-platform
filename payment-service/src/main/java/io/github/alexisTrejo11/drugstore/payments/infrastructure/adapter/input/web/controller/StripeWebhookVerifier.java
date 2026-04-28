package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.input.web.controller;

import io.github.alexisTrejo11.drugstore.payments.core.application.dto.request.StripeWebhookRequest;
import org.springframework.stereotype.Component;

@Component
public class StripeWebhookVerifier {


	public StripeWebhookRequest verifyAndParse(String payload, String stripeSignature) {
		// Aquí iría la lógica para verificar la firma del webhook usando la biblioteca de Stripe
		// y luego parsear el payload a un objeto StripeWebhookRequest.

		// Por simplicidad, este ejemplo asume que la verificación es exitosa y devuelve un objeto de prueba.

		return new StripeWebhookRequest(
				"pi_1234567890",
				"payment_intent.succeeded",
				"ch_1234567890",
				null
		);
	}
}
