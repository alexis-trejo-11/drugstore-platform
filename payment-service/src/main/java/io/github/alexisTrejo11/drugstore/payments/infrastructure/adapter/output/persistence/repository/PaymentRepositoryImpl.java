package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.repository;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Payment;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.PaymentRepository;
import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.entity.PaymentEntity;
import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.jpa.JpaPaymentRepository;
import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.mapper.PaymentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Infrastructure adapter that implements the domain's PaymentRepository port.
 *
 * Responsibilities:
 * - Delegates persistence to JpaPaymentRepository (Spring Data)
 * - Uses PaymentMapper to translate between domain aggregates and JPA entities
 * - Translates value objects (PaymentID, OrderID...) to plain strings for JPA
 *
 * The domain is completely unaware of JPA, Spring Data, or any persistence
 * detail.
 */
@Component
public class PaymentRepositoryImpl implements PaymentRepository {

  private static final Logger logger = LoggerFactory.getLogger(PaymentRepositoryImpl.class);

  private final JpaPaymentRepository jpaRepository;
  private final PaymentMapper mapper;

  public PaymentRepositoryImpl(JpaPaymentRepository jpaRepository, PaymentMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public Payment save(Payment payment) {
    PaymentEntity entity = mapper.toEntity(payment);
    PaymentEntity saved = jpaRepository.save(entity);
    logger.debug("Payment persisted | id={} status={}", saved.getId(), saved.getStatus());
    return mapper.toDomain(saved);
  }

  @Override
  public Optional<Payment> findById(PaymentID id) {
    return jpaRepository.findById(id.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Payment> findByOrderId(OrderID orderId) {
    return jpaRepository.findByOrderId(orderId.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<Payment> findByCustomerId(CustomerID customerId) {
    return jpaRepository.findByCustomerId(customerId.value())
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public boolean existsByOrderId(OrderID orderId) {
    return jpaRepository.existsByOrderId(orderId.value());
  }

  @Override
  public Optional<Payment> findByGatewayPaymentIntentId(String paymentIntentId) {
    return jpaRepository.findByGatewayPaymentId(paymentIntentId)
        .map(mapper::toDomain);
  }

  @Override
  public void deleteById(PaymentID id) {
    jpaRepository.deleteById(id.value());
    logger.debug("Payment deleted | id={}", id.value());
  }
}
