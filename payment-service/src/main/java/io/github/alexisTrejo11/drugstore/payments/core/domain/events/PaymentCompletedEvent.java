package io.github.alexisTrejo11.drugstore.payments.core.domain.events;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Payment;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;
import lombok.Getter;

/**
 * Event emitted when a Payment successfully completes.
 * This triggers the creation of a Sale at the application layer.
 * Other bounded contexts may also listen to this event for their own purposes.
 */
@Getter
public class PaymentCompletedEvent extends DomainEvent {
  private final PaymentID paymentId;
  private final OrderID orderId;
  private final CustomerID customerId;
  private final Money amount;
  private final String gatewayChargeId;

  public PaymentCompletedEvent(PaymentID paymentId, OrderID orderId,
      CustomerID customerId, Money amount, String gatewayChargeId) {
    super("PaymentCompleted");
    this.paymentId = paymentId;
    this.orderId = orderId;
    this.customerId = customerId;
    this.amount = amount;
    this.gatewayChargeId = gatewayChargeId;
  }

	public static PaymentCompletedEvent from(Payment payment, String gatewayChargeId) {
		return new PaymentCompletedEvent(
				payment.getId(),
				payment.getOrderId(),
				payment.getCustomerId(),
				payment.getAmount(),
				gatewayChargeId
		);
	}

  @Override
  public String getAggregateId() {
    return paymentId.value();
  }

  public static PaymentCompletedEvent of(PaymentID paymentId, OrderID orderId,
      CustomerID customerId, Money amount, String gatewayChargeId) {
    return new PaymentCompletedEvent(paymentId, orderId, customerId, amount, gatewayChargeId);
  }
}
