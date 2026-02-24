package io.github.alexisTrejo11.drugstore.payments.core.domain.events;

import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;
import lombok.Getter;

/**
 * Event emitted when a Payment fails during processing.
 * This allows other bounded contexts (e.g., Order Service) to revert or handle
 * the failed payment appropriately.
 */
@Getter
public class PaymentFailedEvent extends DomainEvent {
  private final PaymentID paymentId;
  private final OrderID orderId;
  private final CustomerID customerId;
  private final Money amount;
  private final String failureReason;

  public PaymentFailedEvent(PaymentID paymentId, OrderID orderId,
      CustomerID customerId, Money amount, String failureReason) {
    super("PaymentFailed");
    this.paymentId = paymentId;
    this.orderId = orderId;
    this.customerId = customerId;
    this.amount = amount;
    this.failureReason = failureReason;
  }

  @Override
  public String getAggregateId() {
    return paymentId.value();
  }

  public static PaymentFailedEvent of(PaymentID paymentId, OrderID orderId,
      CustomerID customerId, Money amount, String failureReason) {
    return new PaymentFailedEvent(paymentId, orderId, customerId, amount, failureReason);
  }
}
