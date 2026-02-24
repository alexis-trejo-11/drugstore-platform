package io.github.alexisTrejo11.drugstore.payments.core.domain.events;

import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.SaleID;
import lombok.Getter;

/**
 * Event emitted when a Sale is created from a completed Payment.
 * This notifies other bounded contexts:
 * - Inventory Service: to update stock counts
 * - Notification Service: to send confirmation emails/SMS
 * - Analytics Service: to track sales metrics
 * - Loyalty/Rewards Service: to award points
 */
@Getter
public class SaleCreatedEvent extends DomainEvent {
  private final SaleID saleId;
  private final PaymentID paymentId;
  private final OrderID orderId;
  private final CustomerID customerId;
  private final Money totalAmount;

  public SaleCreatedEvent(SaleID saleId, PaymentID paymentId, OrderID orderId,
      CustomerID customerId, Money totalAmount) {
    super("SaleCreated");
    this.saleId = saleId;
    this.paymentId = paymentId;
    this.orderId = orderId;
    this.customerId = customerId;
    this.totalAmount = totalAmount;
  }

  @Override
  public String getAggregateId() {
    return saleId.value();
  }

  public static SaleCreatedEvent of(SaleID saleId, PaymentID paymentId, OrderID orderId,
      CustomerID customerId, Money totalAmount) {
    return new SaleCreatedEvent(saleId, paymentId, orderId, customerId, totalAmount);
  }
}
