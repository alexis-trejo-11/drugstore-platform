package io.github.alexisTrejo11.drugstore.payments.core.port.output;

import java.util.List;
import java.util.Optional;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Payment;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;

/**
 * Domain-side port for Payment persistence.
 * Implementation lives in the infrastructure layer (JPA adapter).
 */
public interface PaymentRepository {

  Payment save(Payment payment);

  Optional<Payment> findById(PaymentID id);

  Optional<Payment> findByOrderId(OrderID orderId);

  List<Payment> findByCustomerId(CustomerID customerId);

  /**
   * Used to verify idempotency — check if a payment already exists
   * for a given order before creating a new one.
   */
  boolean existsByOrderId(OrderID orderId);

  /**
   * Used when processing Stripe webhooks — we only receive the PaymentIntentId
   * from Stripe, so we need to look up our Payment by the gateway reference.
   */
  Optional<Payment> findByGatewayPaymentIntentId(String paymentIntentId);

  void deleteById(PaymentID id);
}