package io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.repository;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Sale;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.SaleID;
import io.github.alexisTrejo11.drugstore.payments.core.port.output.SaleRepository;
import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.entity.SaleEntity;
import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.jpa.JpaSaleRepository;
import io.github.alexisTrejo11.drugstore.payments.infrastructure.adapter.output.persistence.mapper.SaleMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Infrastructure adapter that implements the domain's SaleRepository port.
 *
 * Responsibilities:
 * - Delegates persistence to JpaSaleRepository (Spring Data)
 * - Uses SaleMapper to translate between domain aggregates and JPA entities
 * - Translates value objects (SaleID, OrderID...) to plain strings for JPA
 *
 * The domain is completely unaware of JPA, Spring Data, or any persistence
 * detail.
 */
@Component
public class SaleRepositoryImpl implements SaleRepository {

  private static final Logger logger = LoggerFactory.getLogger(SaleRepositoryImpl.class);

  private final JpaSaleRepository jpaRepository;
  private final SaleMapper mapper;

  public SaleRepositoryImpl(JpaSaleRepository jpaRepository, SaleMapper mapper) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
  }

  @Override
  public boolean existsByPaymentId(PaymentID paymentId) {
    return jpaRepository.existsByPaymentId(paymentId.value());
  }

  @Override
  public Optional<Sale> findByPaymentId(PaymentID paymentId) {
    return jpaRepository.findByPaymentId(paymentId.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Sale> findById(SaleID saleId) {
    return jpaRepository.findById(saleId.value())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Sale> findByOrderId(OrderID orderID) {
    return jpaRepository.findByOrderId(orderID.value())
        .map(mapper::toDomain);
  }

  @Override
  public List<Sale> findByCustomerId(CustomerID customerId) {
    return jpaRepository.findByCustomerId(customerId.value())
        .stream()
        .map(mapper::toDomain)
        .toList();
  }

  @Override
  public Sale save(Sale sale) {
    SaleEntity entity = mapper.toEntity(sale);
    SaleEntity saved = jpaRepository.save(entity);
    logger.debug("Sale persisted | id={} status={}", saved.getId(), saved.getStatus());
    return mapper.toDomain(saved);
  }
}
