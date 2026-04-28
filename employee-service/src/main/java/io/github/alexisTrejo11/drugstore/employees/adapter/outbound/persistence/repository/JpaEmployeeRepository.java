package io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.entity.EmployeeEntity;

/**
 * Spring Data JPA repository for EmployeeEntity
 * Provides basic CRUD and custom query methods
 */
public interface JpaEmployeeRepository
    extends JpaRepository<EmployeeEntity, String>, JpaSpecificationExecutor<EmployeeEntity> {

  /**
   * Find employee by employee number (excluding soft-deleted)
   */
  @Query(value = "SELECT * FROM employees WHERE employee_number = :employeeNumber AND deleted_at IS NULL", nativeQuery = true)
  Optional<EmployeeEntity> findByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

  /**
   * Check if employee exists by employee number (excluding soft-deleted)
   */
  @Query(value = "SELECT exists(SELECT 1 FROM employees WHERE employee_number = :employeeNumber AND deleted_at IS NULL)", nativeQuery = true)
  boolean existsByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

  /**
   * Check if employee exists by ID (excluding soft-deleted)
   */
  @Query(value = "SELECT exists(SELECT 1 FROM employees WHERE id = :id AND deleted_at IS NULL)", nativeQuery = true)
  boolean existsById(@Param("id") String id);

  /**
   * Find employee by ID (excluding soft-deleted)
   */
  @Query(value = "SELECT * FROM employees WHERE id = :id AND deleted_at IS NULL", nativeQuery = true)
  Optional<EmployeeEntity> findById(@Param("id") String id);

  /**
   * Find employee by ID including soft-deleted records
   */
  @Query(value = "SELECT * FROM employees WHERE id = :id", nativeQuery = true)
  Optional<EmployeeEntity> findByIdIncludeDeleted(@Param("id") String id);

  /**
   * Find employee by employee number including soft-deleted records
   */
  @Query(value = "SELECT * FROM employees WHERE employee_number = :employeeNumber", nativeQuery = true)
  Optional<EmployeeEntity> findByEmployeeNumberIncludeDeleted(@Param("employeeNumber") String employeeNumber);
}
