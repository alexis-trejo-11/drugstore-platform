package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.jpa;

import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.entity.PaymentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for PaymentEntity.
 * This is a pure infrastructure interface — the domain is unaware of it.
 */
@Repository
public interface JpaPaymentRepository extends JpaRepository<PaymentEntity, String> {

  Optional<PaymentEntity> findByOrderId(String orderId);

  List<PaymentEntity> findByCustomerId(String customerId);

  boolean existsByOrderId(String orderId);

  Optional<PaymentEntity> findByGatewayPaymentId(String gatewayPaymentId);
}
