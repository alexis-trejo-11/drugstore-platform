package io.github.alexisTrejo11.drugstore.payments.core.domain.events;

import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.*;
import lombok.Getter;

/**
 * Event emitted when a Payment is refunded (fully or partially).
 * This triggers the update of the corresponding Sale and may notify other
 * systems
 * (e.g., inventory to restock items, notifications to inform customer).
 */
@Getter
public class PaymentRefundedEvent extends DomainEvent {
	private final PaymentID paymentId;
	private final OrderID orderId;
	private final SaleID saleId;
	private final CustomerID customerId;
	private final Money refundAmount;
	private final Money totalRefunded;
	private final String refundReason;
	private final String gatewayRefundId;
	private final boolean isPartialRefund;

	public PaymentRefundedEvent(PaymentID paymentId, OrderID orderId, SaleID saleId, CustomerID customerId,
	                            Money refundAmount, Money totalRefunded, String refundReason,
	                            String gatewayRefundId, boolean isPartialRefund) {
		super("PaymentRefunded");
		this.paymentId = paymentId;
		this.orderId = orderId;
		this.saleId = saleId;
		this.customerId = customerId;
		this.refundAmount = refundAmount;
		this.totalRefunded = totalRefunded;
		this.refundReason = refundReason;
		this.gatewayRefundId = gatewayRefundId;
		this.isPartialRefund = isPartialRefund;
	}

	@Override
	public String getAggregateId() {
		return paymentId.value();
	}

	public static PaymentRefundedEvent fullRefund(PaymentID paymentId, OrderID orderId,
	                                              CustomerID customerId, Money amount,
	                                              String refundReason, String gatewayRefundId, SaleID saleId) {
		return new PaymentRefundedEvent(paymentId, orderId, saleId, customerId, amount, amount, refundReason, gatewayRefundId, false);
	}

	public static PaymentRefundedEvent partialRefund(PaymentID paymentId, OrderID orderId,
	                                                 CustomerID customerId, Money refundAmount,
	                                                 Money totalRefunded, String refundReason,
	                                                 String gatewayRefundId, SaleID saleId) {
		return new PaymentRefundedEvent(paymentId, orderId, saleId, customerId, refundAmount, totalRefunded, refundReason, gatewayRefundId, true);
	}

	public boolean isFullRefund() {
		return !isPartialRefund;
	}
}
