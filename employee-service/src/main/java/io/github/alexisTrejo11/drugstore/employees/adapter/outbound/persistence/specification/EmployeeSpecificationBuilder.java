package io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.employees.core.domain.specification.EmployeeSearchCriteria;
import io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.entity.EmployeeEntity;
import lombok.extern.slf4j.Slf4j;

/**
 * Builder for JPA Specifications based on EmployeeSearchCriteria
 * Enables dynamic query construction with multiple filters
 */
@Component
@Slf4j
public class EmployeeSpecificationBuilder {

  public Specification<EmployeeEntity> build(EmployeeSearchCriteria criteria) {
    return (root, query, cb) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Exact employee number match
      if (criteria.employeeNumber() != null && !criteria.employeeNumber().isEmpty()) {
        predicates.add(cb.equal(root.get("employeeNumber"), criteria.employeeNumber()));
      }

      // Name search (first name OR last name)
      if (criteria.nameLike() != null && !criteria.nameLike().isEmpty()) {
        String nameLike = "%" + criteria.nameLike().toLowerCase() + "%";
        Predicate firstNameLike = cb.like(cb.lower(root.get("firstName")), nameLike);
        Predicate lastNameLike = cb.like(cb.lower(root.get("lastName")), nameLike);
        predicates.add(cb.or(firstNameLike, lastNameLike));
      }

      // Email search
      if (criteria.emailLike() != null && !criteria.emailLike().isEmpty()) {
        predicates.add(cb.like(
            cb.lower(root.get("contactInfo").get("email")),
            "%" + criteria.emailLike().toLowerCase() + "%"));
      }

      // Phone search
      if (criteria.phoneLike() != null && !criteria.phoneLike().isEmpty()) {
        predicates.add(cb.like(
            root.get("contactInfo").get("phoneNumber"),
            "%" + criteria.phoneLike() + "%"));
      }

      // Status filter (multiple statuses)
      if (criteria.statuses() != null && !criteria.statuses().isEmpty()) {
        predicates.add(root.get("status").as(String.class).in(criteria.statuses()));
      }

      // Role filter (multiple roles)
      if (criteria.roles() != null && !criteria.roles().isEmpty()) {
        predicates.add(root.get("role").as(String.class).in(criteria.roles()));
      }

      // Employee type filter
      if (criteria.employeeTypes() != null && !criteria.employeeTypes().isEmpty()) {
        predicates.add(root.get("employeeType").as(String.class).in(criteria.employeeTypes()));
      }

      // Store filter
      if (criteria.storeId() != null && !criteria.storeId().isEmpty()) {
        predicates.add(cb.equal(root.get("storeId"), criteria.storeId()));
      }

      // Department filter
      if (criteria.department() != null && !criteria.department().isEmpty()) {
        predicates.add(cb.like(
            cb.lower(root.get("department")),
            "%" + criteria.department().toLowerCase() + "%"));
      }

      // Hire date range
      if (criteria.hiredAfter() != null) {
        predicates.add(cb.greaterThanOrEqualTo(root.get("hireDate"), criteria.hiredAfter()));
      }

      if (criteria.hiredBefore() != null) {
        predicates.add(cb.lessThanOrEqualTo(root.get("hireDate"), criteria.hiredBefore()));
      }

      // Certification expiring soon
      if (criteria.certificationExpiring() != null && criteria.certificationExpiring()) {
        int daysThreshold = criteria.certificationExpiringDays() != null
            ? criteria.certificationExpiringDays()
            : 30;
        LocalDate expirationThreshold = LocalDate.now().plusDays(daysThreshold);

        var certJoin = root.join("certifications", JoinType.INNER);
        predicates.add(cb.lessThanOrEqualTo(
            certJoin.get("expirationDate"),
            expirationThreshold));
      }

      // Exclude soft-deleted records
      predicates.add(cb.isNull(root.get("deletedAt")));

      log.debug("Built specification with {} predicates", predicates.size());
      return cb.and(predicates.toArray(new Predicate[0]));
    };
  }
}
