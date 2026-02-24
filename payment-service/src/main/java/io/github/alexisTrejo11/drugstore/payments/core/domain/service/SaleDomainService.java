package io.github.alexisTrejo11.drugstore.payments.core.domain.service;

import io.github.alexisTrejo11.drugstore.payments.core.domain.events.PaymentCompletedEvent;
import io.github.alexisTrejo11.drugstore.payments.core.domain.events.PaymentRefundedEvent;
import io.github.alexisTrejo11.drugstore.payments.core.domain.events.SaleCreatedEvent;
import io.github.alexisTrejo11.drugstore.payments.core.domain.exception.SaleBusinessRuleException;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Payment;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Sale;
import io.github.alexisTrejo11.drugstore.payments.core.domain.validation.DomainValidation;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.PaymentEventPublisher;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.PaymentRepository;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.SaleRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Domain Service for Sale business rules.
 *
 * Listens to Payment domain events (via Spring ApplicationEvent)
 * and reacts by creating or updating Sales accordingly.
 *
 * This keeps the Payment and Sale aggregates decoupled:
 * Payment doesn't know about Sale; Sale is a reaction to Payment events.
 */
@Service
public class SaleDomainService {

  private static final Logger logger = LoggerFactory.getLogger(SaleDomainService.class);

  private final SaleRepository saleRepository;
  private final PaymentRepository paymentRepository;
  private final PaymentEventPublisher eventPublisher;

  public SaleDomainService(SaleRepository saleRepository,
      PaymentRepository paymentRepository,
      PaymentEventPublisher eventPublisher) {
    this.saleRepository = saleRepository;
    this.paymentRepository = paymentRepository;
    this.eventPublisher = eventPublisher;
  }

  /**
   * Reacts to PaymentCompletedEvent by creating a new Sale.
   * Idempotent: skips creation if a Sale already exists for this payment.
   */
  @EventListener
  @Transactional
  public void onPaymentCompleted(PaymentCompletedEvent event) {
    logger.info("Handling PaymentCompletedEvent | paymentId={} orderId={}",
        event.getPaymentId().value(), event.getOrderId().value());

    // Idempotency guard — avoid duplicate sales on event replay
    if (saleRepository.existsByPaymentId(event.getPaymentId())) {
      logger.warn("Sale already exists for paymentId={} — skipping creation",
          event.getPaymentId().value());
      return;
    }

    Payment payment = paymentRepository.findById(event.getPaymentId())
        .orElseThrow(() -> new SaleBusinessRuleException(
            "Payment not found for ID: " + event.getPaymentId().value()));

    Sale sale = Sale.fromPayment(payment);
    saleRepository.save(sale);

    SaleCreatedEvent saleCreatedEvent = new SaleCreatedEvent(
        sale.getId(), sale.getPaymentId(), sale.getOrderId(),
        sale.getCustomerId(), sale.getTotalAmount());
    eventPublisher.publish(saleCreatedEvent);

    logger.info("Sale created and SaleCreatedEvent published | saleId={} paymentId={} orderId={}",
        sale.getId().value(), payment.getId().value(), payment.getOrderId().value());
  }

  /**
   * Reacts to PaymentRefundedEvent by updating the Sale refund status.
   * Handles both full and partial refunds.
   */
  @EventListener
  @Transactional
  public void onPaymentRefunded(PaymentRefundedEvent event) {
    logger.info("Handling PaymentRefundedEvent | paymentId={} saleId={} isFullRefund={}",
        event.getPaymentId().value(), event.getSaleId().value(), event.isFullRefund());

    Sale sale = saleRepository.findById(event.getSaleId())
        .orElseThrow(() -> new SaleBusinessRuleException(
            "Sale not found for ID: " + event.getSaleId().value()));

    if (event.isFullRefund()) {
      sale.registerFullRefund();
      logger.info("Full refund registered on Sale | saleId={}", sale.getId().value());
    } else {
      sale.registerPartialRefund(event.getRefundAmount());
      logger.info("Partial refund registered on Sale | saleId={} amount={}",
          sale.getId().value(), event.getRefundAmount());
    }

    saleRepository.save(sale);
  }

  /**
   * Validates that the sale belongs to the requesting customer.
   * Used as a business rule guard before exposing sale data.
   */
  public void validateSaleOwnership(Sale sale, String customerId) {
    DomainValidation.requireNonNull(sale, "Sale");
    DomainValidation.requireNonBlank(customerId, "CustomerID");

    if (!sale.getCustomerId().value().equals(customerId)) {
      throw new SaleBusinessRuleException(
          "Customer " + customerId + " does not own sale " + sale.getId().value());
    }
  }
}
