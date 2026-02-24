package io.github.alexisTrejo11.drugstore.payments.core.domain.model.params;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentMethod;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import lombok.Builder;

/**
 * Parameters for creating a new Payment.
 */
@Builder
public record CreatePaymentParams(
    OrderID orderId,
    CustomerID customerId,
    Money amount,
    PaymentMethod paymentMethod) {
  public CreatePaymentParams {
    if (orderId == null) {
      throw new IllegalArgumentException("OrderID cannot be null");
    }
    if (customerId == null) {
      throw new IllegalArgumentException("CustomerID cannot be null");
    }
    if (amount == null) {
      throw new IllegalArgumentException("Amount cannot be null");
    }
    if (paymentMethod == null) {
      throw new IllegalArgumentException("PaymentMethod cannot be null");
    }
  }
}
