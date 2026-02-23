package io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.mapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.employees.core.domain.model.Employee;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.Address;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.Certification;
import io.github.alexisTrejo11.drugstore.employees.core.domain.valueobject.ContactInfo;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response.AddressResponse;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response.CertificationResponse;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response.ContactInfoResponse;
import io.github.alexisTrejo11.drugstore.employees.adapter.inbound.rest.dto.response.EmployeeResponse;
import libs_kernel.page.PageResponse;

/**
 * Mapper for converting Employee domain to EmployeeResponse DTO
 */
@Component
public class EmployeeResponseMapper  {

	public EmployeeResponse toResponse(Employee employee) {
		if (employee == null) {
			return null;
		}

		return EmployeeResponse.builder()
				.id(employee.getId() != null ? employee.getId().getValue() : null)
				.employeeNumber(employee.getEmployeeNumber() != null ? employee.getEmployeeNumber().getValue() : null)
				.firstName(employee.getFirstName())
				.lastName(employee.getLastName())
				.dateOfBirth(employee.getDateOfBirth())
				.address(toAddressResponse(employee.getAddress()))
				.contactInfo(toContactInfoResponse(employee.getContactInfo()))
				.role(employee.getRole() != null ? employee.getRole().name() : null)
				.employeeType(employee.getEmployeeType() != null ? employee.getEmployeeType().name() : null)
				.status(employee.getStatus() != null ? employee.getStatus().name() : null)
				.department(employee.getDepartment())
				.storeId(employee.getStoreId())
				.hireDate(employee.getHireDate())
				.terminationDate(employee.getTerminationDate())
				.certifications(toCertificationResponses(employee.getCertifications()))
				.hourlyRate(employee.getHourlyRate())
				.weeklyHours(employee.getWeeklyHours())
				.canWork(employee.canWork())
				.yearsOfService(employee.getYearsOfService())
				.createdAt(employee.getCreatedAt())
				.updatedAt(employee.getUpdatedAt())
				.createdBy(employee.getCreatedBy())
				.lastModifiedBy(employee.getLastModifiedBy())
				.build();
	}


	public PageResponse<EmployeeResponse> toResponsePage(Page<Employee> employees) {
		if (employees == null) {
			return null;
		}

		return PageResponse.from(employees.map(this::toResponse));
	}

	private AddressResponse toAddressResponse(Address address) {
		if (address == null) {
			return null;
		}

		return new AddressResponse(
				address.getStreet(),
				address.getCity(),
				address.getState(),
				address.getPostalCode(),
				address.getCountry());
	}

	private ContactInfoResponse toContactInfoResponse(ContactInfo contactInfo) {
		if (contactInfo == null) {
			return null;
		}

		return new ContactInfoResponse(
				contactInfo.getEmail(),
				contactInfo.getPhoneNumber(),
				contactInfo.getEmergencyContact(),
				contactInfo.getEmergencyPhone()
		);
	}

	private List<CertificationResponse> toCertificationResponses(List<Certification> certifications) {
		if (certifications == null) {
			return Collections.emptyList();
		}

		return certifications.stream()
				.map(this::toCertificationResponse)
				.collect(Collectors.toList());
	}

	private CertificationResponse toCertificationResponse(Certification certification) {
		if (certification == null) {
			return null;
		}

		return new CertificationResponse(
				certification.getType() != null ? certification.getType().name() : null,
				certification.getLicenseNumber(),
				certification.getIssuingAuthority(),
				certification.getIssueDate(),
				certification.getExpirationDate(),
				certification.isValid(),
				certification.isExpired());
	}
}
