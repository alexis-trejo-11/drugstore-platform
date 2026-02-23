package io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.repository;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import io.github.alexisTrejo11.drugstore.employees.core.port.output.EmployeeRepository;
import io.github.alexisTrejo11.drugstore.employees.core.domain.model.Employee;
import io.github.alexisTrejo11.drugstore.employees.core.domain.specification.EmployeeSearchCriteria;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeId;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.EmployeeNumber;
import io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.entity.EmployeeEntity;
import io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.mapper.EmployeeEntityMapper;
import io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.specification.EmployeeSpecificationBuilder;
import lombok.extern.slf4j.Slf4j;

/**
 * Implementation of EmployeeRepository using JPA
 * Handles conversion between domain and persistence models
 */
@Repository
@Slf4j
public class EmployeeRepositoryImpl implements EmployeeRepository {

  private final JpaEmployeeRepository jpaRepository;
  private final EmployeeEntityMapper mapper;
  private final EmployeeSpecificationBuilder specificationBuilder;

  @Autowired
  public EmployeeRepositoryImpl(
      JpaEmployeeRepository jpaRepository,
      EmployeeEntityMapper mapper,
      EmployeeSpecificationBuilder specificationBuilder) {
    this.jpaRepository = jpaRepository;
    this.mapper = mapper;
    this.specificationBuilder = specificationBuilder;
  }

  @Override
  public Employee save(Employee employee) {
    log.debug("Saving employee: {}", employee.getId());

    EmployeeEntity entity = mapper.fromDomain(employee);
    EmployeeEntity saved = jpaRepository.save(entity);
    Employee result = mapper.toDomain(saved);

    log.info("Employee saved successfully: {}", result.getId());
    return result;
  }

  @Override
  public Optional<Employee> findById(EmployeeId id) {
    log.debug("Finding employee by ID: {}", id);

    return jpaRepository.findById(id.getValue())
        .map(mapper::toDomain);
  }

  @Override
  public Optional<Employee> findByEmployeeNumber(EmployeeNumber employeeNumber) {
    log.debug("Finding employee by employee number: {}", employeeNumber);

    return jpaRepository.findByEmployeeNumber(employeeNumber.getValue())
        .map(mapper::toDomain);
  }

  @Override
  public Page<Employee> search(EmployeeSearchCriteria criteria) {
    log.debug("Searching employees with criteria: page={}, size={}", criteria.page(), criteria.size());

    Specification<EmployeeEntity> spec = specificationBuilder.build(criteria);
    PageRequest pageRequest = buildPageRequest(criteria);

    Page<EmployeeEntity> entityPage = jpaRepository.findAll(spec, pageRequest);
    Page<Employee> result = mapper.toDomainPage(entityPage);

    log.debug("Found {} employees (total: {})", result.getNumberOfElements(), result.getTotalElements());
    return result;
  }

  @Override
  public long count(EmployeeSearchCriteria criteria) {
    log.debug("Counting employees with criteria");

    Specification<EmployeeEntity> spec = specificationBuilder.build(criteria);
    long count = jpaRepository.count(spec);

    log.debug("Employee count: {}", count);
    return count;
  }

  @Override
  public boolean existsById(EmployeeId id) {
    log.debug("Checking if employee exists by ID: {}", id);

    return jpaRepository.existsById(id.getValue());
  }

  @Override
  public boolean existsByEmployeeNumber(EmployeeNumber employeeNumber) {
    log.debug("Checking if employee exists by employee number: {}", employeeNumber);

    return jpaRepository.existsByEmployeeNumber(employeeNumber.getValue());
  }

  @Override
  public void deleteById(EmployeeId id) {
    log.info("Soft deleting employee: {}", id);

    jpaRepository.findById(id.getValue())
        .ifPresentOrElse(
            entity -> {
              entity.markAsDeleted();
              jpaRepository.save(entity);
              log.info("Employee soft deleted: {}", id);
            },
            () -> log.warn("Attempted to delete non-existing employee: {}", id));
  }

  @Override
  public void restoreById(EmployeeId id) {
    log.info("Restoring employee: {}", id);

    Optional<EmployeeEntity> optional = jpaRepository.findByIdIncludeDeleted(id.getValue());
    if (optional.isPresent()) {
      EmployeeEntity entity = optional.get();
      entity.markAsRestored();
      jpaRepository.save(entity);
      log.info("Employee restored: {}", id);
    } else {
      log.warn("Attempted to restore non-existing employee: {}", id);
    }
  }

  /**
   * Build PageRequest from search criteria
   */
  private PageRequest buildPageRequest(EmployeeSearchCriteria criteria) {
    Sort sort = buildSort(criteria);
    return PageRequest.of(
        criteria.page(),
        criteria.size(),
        sort);
  }

  /**
   * Build Sort from search criteria
   */
  private Sort buildSort(EmployeeSearchCriteria criteria) {
    if (criteria.sortBy() == null) {
      return Sort.by("lastName").ascending();
    }

    String field = criteria.sortBy().field();
    String direction = criteria.sortBy().direction();

    return "ASC".equals(direction)
        ? Sort.by(field).ascending()
        : Sort.by(field).descending();
  }
}
