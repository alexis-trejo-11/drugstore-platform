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
  @Query("SELECT e FROM EmployeeEntity e WHERE e.employeeNumber = :employeeNumber AND e.deletedAt IS NULL")
  Optional<EmployeeEntity> findByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

  /**
   * Check if employee exists by employee number (excluding soft-deleted)
   */
  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EmployeeEntity e WHERE e.employeeNumber = :employeeNumber AND e.deletedAt IS NULL")
  boolean existsByEmployeeNumber(@Param("employeeNumber") String employeeNumber);

  /**
   * Check if employee exists by ID (excluding soft-deleted)
   */
  @Query("SELECT CASE WHEN COUNT(e) > 0 THEN true ELSE false END FROM EmployeeEntity e WHERE e.id = :id AND e.deletedAt IS NULL")
  boolean existsById(@Param("id") String id);

  /**
   * Find employee by ID (excluding soft-deleted)
   */
  @Query("SELECT e FROM EmployeeEntity e WHERE e.id = :id AND e.deletedAt IS NULL")
  Optional<EmployeeEntity> findById(@Param("id") String id);

  /**
   * Find employee by ID including soft-deleted records
   */
  @Query("SELECT e FROM EmployeeEntity e WHERE e.id = :id")
  Optional<EmployeeEntity> findByIdIncludeDeleted(@Param("id") String id);

  /**
   * Find employee by employee number including soft-deleted records
   */
  @Query("SELECT e FROM EmployeeEntity e WHERE e.employeeNumber = :employeeNumber")
  Optional<EmployeeEntity> findByEmployeeNumberIncludeDeleted(@Param("employeeNumber") String employeeNumber);
}
