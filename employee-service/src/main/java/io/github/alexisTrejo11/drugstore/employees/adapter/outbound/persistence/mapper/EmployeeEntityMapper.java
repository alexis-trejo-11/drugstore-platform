package io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.employees.core.domain.model.Employee;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.*;
import io.github.alexisTrejo11.drugstore.employees.adapter.outbound.persistence.entity.*;
import lombok.extern.slf4j.Slf4j;

/**
 * Mapper for converting between Employee domain model and EmployeeEntity
 * Handles null-safety and value object creation/destruction
 */
@Component
@Slf4j
public class EmployeeEntityMapper  {

  public EmployeeEntity fromDomain(Employee employee) {
    if (employee == null) {
      return null;
    }

    log.debug("Converting Employee domain to entity: {}", employee.getId());

    EmployeeEntity entity = new EmployeeEntity();

    // Identity
    entity.setId(employee.getId() != null ? employee.getId().getValue() : null);
    entity.setEmployeeNumber(employee.getEmployeeNumber() != null ? employee.getEmployeeNumber().getValue() : null);

    // Personal Information
    entity.setFirstName(employee.getFirstName());
    entity.setLastName(employee.getLastName());
    entity.setDateOfBirth(employee.getDateOfBirth());
    entity.setAddress(toAddressEmbeddable(employee.getAddress()));
    entity.setContactInfo(toContactInfoEmbeddable(employee.getContactInfo()));

    // Employment Details
    entity.setRole(toRoleEnum(employee.getRole()));
    entity.setEmployeeType(toEmployeeTypeEnum(employee.getEmployeeType()));
    entity.setStatus(toStatusEnum(employee.getStatus()));
    entity.setDepartment(employee.getDepartment());
    entity.setStoreId(employee.getStoreId());
    entity.setHireDate(employee.getHireDate());
    entity.setTerminationDate(employee.getTerminationDate());

    // Certifications
    entity.setCertifications(toCertificationEntities(employee.getCertifications(), employee.getId()));

    // Compensation
    entity.setHourlyRate(employee.getHourlyRate());
    entity.setWeeklyHours(employee.getWeeklyHours());

    // Audit
    entity.setCreatedAt(employee.getCreatedAt());
    entity.setUpdatedAt(employee.getUpdatedAt());
    entity.setCreatedBy(employee.getCreatedBy());
    entity.setLastModifiedBy(employee.getLastModifiedBy());

    return entity;
  }

  public Employee toDomain(EmployeeEntity entity) {
    if (entity == null) {
      return null;
    }

    log.debug("Converting EmployeeEntity to domain: {}", entity.getId());

    Employee employee = new Employee();

    // Identity
    if (entity.getId() != null) {
      employee.setId(EmployeeId.of(entity.getId()));
    }
    if (entity.getEmployeeNumber() != null) {
      employee.setEmployeeNumber(EmployeeNumber.of(entity.getEmployeeNumber()));
    }

    // Personal Information
    employee.setFirstName(entity.getFirstName());
    employee.setLastName(entity.getLastName());
    employee.setDateOfBirth(entity.getDateOfBirth());
    employee.setAddress(toAddressDomain(entity.getAddress()));
    employee.setContactInfo(toContactInfoDomain(entity.getContactInfo()));

    // Employment Details
    employee.setRole(toRoleDomain(entity.getRole()));
    employee.setEmployeeType(toEmployeeTypeDomain(entity.getEmployeeType()));
    employee.setStatus(toStatusDomain(entity.getStatus()));
    employee.setDepartment(entity.getDepartment());
    employee.setStoreId(entity.getStoreId());
    employee.setHireDate(entity.getHireDate());
    employee.setTerminationDate(entity.getTerminationDate());

    // Certifications
    employee.setCertifications(toCertificationDomainList(entity.getCertifications()));

    // Compensation
    employee.setHourlyRate(entity.getHourlyRate());
    employee.setWeeklyHours(entity.getWeeklyHours());

    // Audit
    employee.setCreatedAt(entity.getCreatedAt());
    employee.setUpdatedAt(entity.getUpdatedAt());
    employee.setCreatedBy(entity.getCreatedBy());
    employee.setLastModifiedBy(entity.getLastModifiedBy());

    return employee;
  }

  public Page<Employee> toDomainPage(Page<EmployeeEntity> entityPage) {
    if (entityPage == null) {
      return Page.empty();
    }

    List<Employee> employees = entityPage.getContent().stream()
        .map(this::toDomain)
        .collect(Collectors.toList());

    return new PageImpl<>(employees, entityPage.getPageable(), entityPage.getTotalElements());
  }

  // Helper methods for Address
  private AddressEmbeddable toAddressEmbeddable(Address address) {
    if (address == null) {
      return null;
    }
    return new AddressEmbeddable(
        address.getStreet(),
        address.getCity(),
        address.getState(),
        address.getPostalCode(),
        address.getCountry());
  }

  private Address toAddressDomain(AddressEmbeddable embeddable) {
    if (embeddable == null || embeddable.getStreet() == null) {
      return null;
    }
    return Address.of(
        embeddable.getStreet(),
        embeddable.getCity(),
        embeddable.getState(),
        embeddable.getPostalCode(),
        embeddable.getCountry());
  }

  // Helper methods for ContactInfo
  private ContactInfoEmbeddable toContactInfoEmbeddable(ContactInfo contactInfo) {
    if (contactInfo == null) {
      return null;
    }
    return new ContactInfoEmbeddable(
        contactInfo.getEmail(),
        contactInfo.getPhoneNumber(),
        contactInfo.getEmergencyContact(),
        contactInfo.getEmergencyPhone());
  }

  private ContactInfo toContactInfoDomain(ContactInfoEmbeddable embeddable) {
    if (embeddable == null || embeddable.getEmail() == null) {
      return null;
    }
    return ContactInfo.of(
        embeddable.getEmail(),
        embeddable.getPhoneNumber(),
        embeddable.getEmergencyContact(),
        embeddable.getEmergencyPhone());
  }

  // Helper methods for Certifications
  private List<CertificationEntity> toCertificationEntities(List<Certification> certifications, EmployeeId employeeId) {
    if (certifications == null || certifications.isEmpty()) {
      return new ArrayList<>();
    }

    String empId = employeeId != null ? employeeId.getValue() : null;

    return certifications.stream()
        .map(cert -> toCertificationEntity(cert, empId))
        .collect(Collectors.toList());
  }

  private CertificationEntity toCertificationEntity(Certification cert, String employeeId) {
    if (cert == null) {
      return null;
    }

    CertificationEntity entity = new CertificationEntity();
    entity.setLicenseNumber(cert.getLicenseNumber());
    entity.setIssuingAuthority(cert.getIssuingAuthority());
    entity.setIssueDate(cert.getIssueDate());
    entity.setExpirationDate(cert.getExpirationDate());
    entity.setType(toCertificationTypeEnum(cert.getType()));
    entity.setEmployeeId(employeeId);

    return entity;
  }

  private List<Certification> toCertificationDomainList(List<CertificationEntity> entities) {
    if (entities == null || entities.isEmpty()) {
      return new ArrayList<>();
    }

    return entities.stream()
        .map(this::toCertificationDomain)
        .filter(cert -> cert != null)
        .collect(Collectors.toList());
  }

  private Certification toCertificationDomain(CertificationEntity entity) {
    if (entity == null) {
      return null;
    }

    try {
      return Certification.of(
          entity.getLicenseNumber(),
          entity.getIssuingAuthority(),
          entity.getIssueDate(),
          entity.getExpirationDate(),
          toCertificationTypeDomain(entity.getType()));
    } catch (Exception e) {
      log.warn("Failed to convert CertificationEntity to domain: {}", e.getMessage());
      return null;
    }
  }

  // Helper methods for CertificationType
  private CertificationEntity.CertificationTypeEnum toCertificationTypeEnum(Certification.CertificationType type) {
    if (type == null) {
      return null;
    }

    return switch (type) {
      case PHARMACIST_LICENSE -> CertificationEntity.CertificationTypeEnum.PHARMACIST_LICENSE;
      case PHARMACY_TECHNICIAN_LICENSE -> CertificationEntity.CertificationTypeEnum.PHARMACY_TECHNICIAN_CERTIFICATION;
      case SPECIALIZED_TRAINING -> CertificationEntity.CertificationTypeEnum.OTHER;
      case CPR_CERTIFICATION -> CertificationEntity.CertificationTypeEnum.CPR_CERTIFICATION;
      case CONTROLLED_SUBSTANCE_LICENSE -> CertificationEntity.CertificationTypeEnum.OTHER;
      case IMMUNIZATION_CERTIFICATION -> CertificationEntity.CertificationTypeEnum.IMMUNIZATION_CERTIFICATION;
      default -> CertificationEntity.CertificationTypeEnum.OTHER;
    };
  }

  private Certification.CertificationType toCertificationTypeDomain(CertificationEntity.CertificationTypeEnum type) {
    if (type == null) {
      return null;
    }

    return switch (type) {
      case PHARMACIST_LICENSE -> Certification.CertificationType.PHARMACIST_LICENSE;
      case PHARMACY_TECHNICIAN_CERTIFICATION -> Certification.CertificationType.PHARMACY_TECHNICIAN_LICENSE;
      case CPR_CERTIFICATION -> Certification.CertificationType.CPR_CERTIFICATION;
      case FIRST_AID_CERTIFICATION -> Certification.CertificationType.SPECIALIZED_TRAINING;
      case IMMUNIZATION_CERTIFICATION -> Certification.CertificationType.IMMUNIZATION_CERTIFICATION;
      case OTHER -> Certification.CertificationType.SPECIALIZED_TRAINING;
      default -> Certification.CertificationType.SPECIALIZED_TRAINING;
    };
  }

  // Helper methods for EmployeeRole
  private EmployeeEntity.EmployeeRoleEnum toRoleEnum(EmployeeRole role) {
    if (role == null) {
      return null;
    }
    return EmployeeEntity.EmployeeRoleEnum.valueOf(role.name());
  }

  private EmployeeRole toRoleDomain(EmployeeEntity.EmployeeRoleEnum roleEnum) {
    if (roleEnum == null) {
      return null;
    }
    return EmployeeRole.valueOf(roleEnum.name());
  }

  // Helper methods for EmployeeType
  private EmployeeEntity.EmployeeTypeEnum toEmployeeTypeEnum(EmployeeType type) {
    if (type == null) {
      return null;
    }
    return EmployeeEntity.EmployeeTypeEnum.valueOf(type.name());
  }

  private EmployeeType toEmployeeTypeDomain(EmployeeEntity.EmployeeTypeEnum typeEnum) {
    if (typeEnum == null) {
      return null;
    }
    return EmployeeType.valueOf(typeEnum.name());
  }

  // Helper methods for EmployeeStatus
  private EmployeeEntity.EmployeeStatusEnum toStatusEnum(EmployeeStatus status) {
    if (status == null) {
      return null;
    }
    return EmployeeEntity.EmployeeStatusEnum.valueOf(status.name());
  }

  private EmployeeStatus toStatusDomain(EmployeeEntity.EmployeeStatusEnum statusEnum) {
    if (statusEnum == null) {
      return null;
    }
    return EmployeeStatus.valueOf(statusEnum.name());
  }
}
