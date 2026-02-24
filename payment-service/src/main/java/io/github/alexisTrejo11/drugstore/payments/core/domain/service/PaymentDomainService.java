package io.github.alexisTrejo11.drugstore.payments.core.domain.service;

import io.github.alexisTrejo11.drugstore.payments.core.domain.events.PaymentCompletedEvent;
import io.github.alexisTrejo11.drugstore.payments.core.domain.events.PaymentFailedEvent;
import io.github.alexisTrejo11.drugstore.payments.core.domain.events.PaymentRefundedEvent;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Sale;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentStatus;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentGatewayRef;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.PaymentEventPublisher;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.github.alexisTrejo11.drugstore.payments.core.domain.exception.PaymentBusinessRuleException;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Payment;
import io.github.alexisTrejo11.drugstore.payments.core.domain.validation.DomainValidation;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.PaymentRepository;

/**
 * Domain Service for Payment business rules.
 * <p>
 * Handles operations that:
 * - Span multiple aggregates or require repository lookups for rule validation
 * - Contain pure business logic not belonging to a single aggregate
 * <p>
 * Does NOT contain application concerns (transactions, DTOs, HTTP).
 */
@Service
public class PaymentDomainService {

	private static final Logger logger = LoggerFactory.getLogger(PaymentDomainService.class);

	private final PaymentRepository paymentRepository;
	private final PaymentEventPublisher eventPublisher;

	public PaymentDomainService(PaymentRepository paymentRepository,
	                            PaymentEventPublisher eventPublisher) {
		this.paymentRepository = paymentRepository;
		this.eventPublisher = eventPublisher;
	}

	/**
	 * Validates that no active payment already exists for the given order.
	 * Prevents duplicate payments for the same order (idempotency guard).
	 */
	public void validateNoDuplicatePayment(Payment payment) {
		DomainValidation.requireNonNull(payment, "Payment");

		boolean exists = paymentRepository.existsByOrderId(payment.getOrderId());
		if (exists) {
			throw new PaymentBusinessRuleException(
					"An active payment already exists for order: " + payment.getOrderId().value());
		}

		logger.debug("Duplicate payment check passed | orderId={}", payment.getOrderId().value());
	}

	/**
	 * Transitions a payment to PROCESSING after gateway ref is registered.
	 * Publishes no event here — event is published after gateway confirmation.
	 */
	public void startProcessing(Payment payment, PaymentGatewayRef gatewayRef) {
		DomainValidation.requireNonNull(payment, "Payment");
		DomainValidation.requireNonNull(gatewayRef, "PaymentGatewayRef");

		payment.startProcessing(gatewayRef);
		paymentRepository.save(payment);

		logger.info("Payment moved to PROCESSING | id={} gateway={} gatewayPaymentId={}",
				payment.getId().value(), gatewayRef.gateway(), gatewayRef.gatewayPaymentId());
	}

	/**
	 * Confirms a payment after gateway webhook confirms the charge.
	 * Transitions to COMPLETED and publishes PaymentCompletedEvent.
	 * The SaleDomainService listens to this event to create the Sale.
	 */
	public void confirmPayment(Payment payment, String gatewayChargeId) {
		DomainValidation.requireNonNull(payment, "Payment");

		if (payment.getStatus() != PaymentStatus.PROCESSING) {
			throw new PaymentBusinessRuleException(
					"Cannot confirm a payment that is not PROCESSING. Status: " + payment.getStatus());
		}

		payment.complete(gatewayChargeId);
		paymentRepository.save(payment);

		PaymentCompletedEvent event = PaymentCompletedEvent.from(payment, gatewayChargeId);
		eventPublisher.publish(event);

		logger.info("Payment confirmed and event published | id={} orderId={} chargeId={}",
				payment.getId().value(), payment.getOrderId().value(), gatewayChargeId);
	}

	/**
	 * Marks a payment as FAILED and publishes PaymentFailedEvent.
	 * Used when the Stripe webhook reports a payment_intent.payment_failed event.
	 */
	public void failPayment(Payment payment, String reason) {
		DomainValidation.requireNonNull(payment, "Payment");
		DomainValidation.requireNonBlank(reason, "Failure reason");

		payment.fail(reason);
		paymentRepository.save(payment);

		PaymentFailedEvent event = new PaymentFailedEvent(
				payment.getId(), payment.getOrderId(),
				payment.getCustomerId(), payment.getAmount(), reason);
		eventPublisher.publish(event);

		logger.warn("Payment failed and event published | id={} orderId={} reason={}",
				payment.getId().value(), payment.getOrderId().value(), reason);
	}

	/**
	 * Processes a full refund on the payment after gateway confirms it.
	 * Publishes PaymentRefundedEvent so the Sale is updated.
	 */
	public void processFullRefund(Payment payment, Sale sale,
	                              String reason, String gatewayRefundId) {
		DomainValidation.requireNonNull(payment, "Payment");
		DomainValidation.requireNonNull(sale, "Sale");
		DomainValidation.requireNonBlank(reason, "Refund reason");

		if (!payment.isRefundable()) {
			throw new PaymentBusinessRuleException(
					"Payment is not refundable. Status: " + payment.getStatus());
		}

		Money refundAmount = payment.getAmount();
		payment.refund(reason, gatewayRefundId);
		paymentRepository.save(payment);

		PaymentRefundedEvent event = new PaymentRefundedEvent(
				payment.getId(), payment.getOrderId(), sale.getId(), payment.getCustomerId(), refundAmount, refundAmount, reason, gatewayRefundId, false);
		eventPublisher.publish(event);

		logger.info("Full refund processed and event published | paymentId={} saleId={} amount={}",
				payment.getId().value(), sale.getId().value(), refundAmount);
	}

	/**
	 * Processes a partial refund on the payment.
	 * Validates the refund amount doesn't exceed remaining refundable amount.
	 */
	public void processPartialRefund(Payment payment, Sale sale,
	                                 Money refundAmount, String reason, String gatewayRefundId) {
		DomainValidation.requireNonNull(payment, "Payment");
		DomainValidation.requireNonNull(sale, "Sale");
		DomainValidation.requireNonNull(refundAmount, "Refund amount");
		DomainValidation.requireNonBlank(reason, "Refund reason");

		if (!payment.isRefundable()) {
			throw new PaymentBusinessRuleException(
					"Payment is not refundable. Status: " + payment.getStatus());
		}

		Money remaining = payment.getRemainingRefundableAmount();
		if (refundAmount.isGreaterThan(remaining)) {
			throw new PaymentBusinessRuleException(
					"Refund amount " + refundAmount + " exceeds remaining refundable amount " + remaining);
		}

		payment.partialRefund(refundAmount, reason, gatewayRefundId);
		paymentRepository.save(payment);

		PaymentRefundedEvent event = new PaymentRefundedEvent(
				payment.getId(), payment.getOrderId(), sale.getId(), payment.getCustomerId(), refundAmount, payment.getAmount(), reason, gatewayRefundId, true);
		eventPublisher.publish(event);

		logger.info("Partial refund processed and event published | paymentId={} saleId={} refundAmount={} remaining={}",
				payment.getId().value(), sale.getId().value(), refundAmount,
				payment.getRemainingRefundableAmount());
	}
}
