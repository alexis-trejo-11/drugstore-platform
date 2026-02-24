package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.jpa;

import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.entity.SaleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for SaleEntity.
 * This is a pure infrastructure interface — the domain is unaware of it.
 */
@Repository
public interface JpaSaleRepository extends JpaRepository<SaleEntity, String> {

  Optional<SaleEntity> findByPaymentId(String paymentId);

  Optional<SaleEntity> findByOrderId(String orderId);

  List<SaleEntity> findByCustomerId(String customerId);

  boolean existsByPaymentId(String paymentId);
}
