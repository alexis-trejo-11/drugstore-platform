package io.github.alexisTrejo11.drugstore.address.service;

import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressRequest;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressResponse;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressSummaryResponse;
import io.github.alexisTrejo11.drugstore.address.utils.exceptions.*;
import io.github.alexisTrejo11.drugstore.address.entity.AddressEntity;
import io.github.alexisTrejo11.drugstore.address.repository.AddressRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

	private final AddressRepository addressRepository;
	private static final int CUSTOMER_MAX_ADDRESSES = 5;
	private static final int EMPLOYEE_MAX_ADDRESSES = 1;


	@Transactional(readOnly = true)
	public AddressResponse findAddressById(String addressId) {
		AddressEntity entity = findActiveAddressById(addressId);
		return mapToResponse(entity);
	}

	@Transactional(readOnly = true)
	public AddressResponse findAddressByIdAndUserId(String addressId, String userId) {
		UUID id = parseUUID(addressId);

		AddressEntity entity = addressRepository.findByIdAndUserIdAndActiveTrue(id, userId)
				.orElseThrow(() -> new AddressNotFoundException(addressId, userId));
		return mapToResponse(entity);
	}

	@Transactional(readOnly = true)
	public List<AddressResponse> findAddressesByUserId(String userId) {
		return addressRepository.findByUserIdAndActiveTrue(userId).stream()
				.map(this::mapToResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public List<AddressSummaryResponse> findAddressSummariesByUserId(String userId) {
		return addressRepository.findByUserIdAndActiveTrue(userId).stream()
				.map(this::mapToSummaryResponse)
				.collect(Collectors.toList());
	}

	@Transactional(readOnly = true)
	public Page<AddressSummaryResponse> findAllAddresses(Pageable pageable) {
		return addressRepository.findByActiveTrue(pageable)
				.map(this::mapToSummaryResponse);
	}

	@Transactional
	public AddressResponse createAddress(String userId, AddressRequest request) {
		validateAddress(request);

		AddressEntity.UserType userType = determineUserType(userId);

		validateAddressLimit(userId, userType);

		AddressEntity entity = buildAddressEntity(userId, userType, request);

		if (Boolean.TRUE.equals(request.isDefault()) || isFirstAddress(userId)) {
			handleDefaultAddress(entity);
		}

		AddressEntity savedEntity = addressRepository.save(entity);
		log.info("Address created successfully for user: {}, addressId: {}", userId, savedEntity.getId());

		return mapToResponse(savedEntity);
	}

	@Transactional
	public AddressResponse updateAddress(String addressId, AddressRequest request) {
		return updateAddress(addressId, request, null);
	}

	@Transactional
	public AddressResponse updateAddress(String addressId, AddressRequest request, String userId) {
		validateAddress(request);

		UUID id = parseUUID(addressId);
		AddressEntity entity;

		if (userId != null) {
			entity = addressRepository.findByIdAndUserIdAndActiveTrue(id, userId)
					.orElseThrow(() -> new UnauthorizedAddressAccessException(addressId, userId));
		} else {
			entity = findActiveAddressById(addressId);
		}

		updateAddressEntity(entity, request);

		if (Boolean.TRUE.equals(request.isDefault()) && !entity.getIsDefault()) {
			handleDefaultAddress(entity);
		}

		AddressEntity updatedEntity = addressRepository.save(entity);
		log.info("Address updated successfully: {}", addressId);

		return mapToResponse(updatedEntity);
	}

	@Transactional
	public void deleteAddress(String addressId) {
		AddressEntity entity = findActiveAddressById(addressId);
		softDeleteAddress(entity);
		log.info("Address deleted successfully: {}", addressId);
	}

	@Transactional
	public void deleteAddress(String addressId, String userId) {
		UUID id = parseUUID(addressId);

		AddressEntity entity = addressRepository.findByIdAndUserIdAndActiveTrue(id, userId)
				.orElseThrow(() -> new UnauthorizedAddressAccessException(addressId, userId));
		softDeleteAddress(entity);
		log.info("Address deleted successfully by user: {}, addressId: {}", userId, addressId);
	}

	@Transactional
	public AddressResponse setAddressAsDefault(String addressId, String userId) {
		addressRepository.resetDefaultAddressForUser(userId);

		UUID uuid = parseUUID(addressId);
		AddressEntity entity = addressRepository.findByIdAndUserIdAndActiveTrue(uuid, userId)
				.orElseThrow(() -> new AddressNotFoundException(addressId, userId));

		entity.setIsDefault(true);
		AddressEntity updatedEntity = addressRepository.save(entity);

		log.info("Address set as default: {} for user: {}", addressId, userId);
		return mapToResponse(updatedEntity);
	}

	private AddressEntity findActiveAddressById(String addressId) {
		UUID uuid = parseUUID(addressId);
		return addressRepository.findByIdAndActiveTrue(uuid)
				.orElseThrow(() -> new AddressNotFoundException(addressId));
	}

	private void validateAddress(AddressRequest request) {
		if (request.street() == null || request.street().trim().isEmpty()) {
			throw new InvalidAddressException("Street is required");
		}
		if (request.city() == null || request.city().trim().isEmpty()) {
			throw new InvalidAddressException("City is required");
		}
		if (request.state() == null || request.state().trim().isEmpty()) {
			throw new InvalidAddressException("State is required");
		}
		if (request.country() == null || request.country().trim().isEmpty()) {
			throw new InvalidAddressException("Country is required");
		}
		if (request.postalCode() == null || request.postalCode().trim().isEmpty()) {
			throw new InvalidAddressException("Postal code is required");
		}

		// Validación específica para código postal según país (ejemplo básico)
		validatePostalCodeFormat(request.country(), request.postalCode());
	}

	private void validatePostalCodeFormat(String country, String postalCode) {
		// Reglas básicas de validación por país
		boolean isValid = switch (country) {
			case "US" -> postalCode.matches("^\\d{5}(-\\d{4})?$");
			case "ES", "MX" -> postalCode.matches("^\\d{5}$");
			case "CA" -> postalCode.matches("^[A-Z]\\d[A-Z] \\d[A-Z]\\d$");
			case "UK" -> postalCode.matches("^[A-Z]{1,2}\\d{1,2}[A-Z]? \\d[A-Z]{2}$");
			default -> true; // Para países sin validación específica
		};

		if (!isValid) {
			throw new InvalidAddressException("Invalid postal code format for country: " + country);
		}
	}

	private void validateAddressLimit(String userId, AddressEntity.UserType userType) {
		long currentAddressCount = addressRepository.countByUserIdAndActiveTrue(userId);
		int limit = getUserAddressLimit(userType);

		if (currentAddressCount >= limit) {
			throw new AddressLimitExceededException(userId, limit, userType.name());
		}
	}

	private int getUserAddressLimit(AddressEntity.UserType userType) {
		return userType == AddressEntity.UserType.CUSTOMER ?
				CUSTOMER_MAX_ADDRESSES : EMPLOYEE_MAX_ADDRESSES;
	}

	private AddressEntity.UserType determineUserType(String userId) {
		// En una aplicación real, esto podría venir de un servicio de usuarios
		// o del contexto de seguridad. Para este ejemplo, usamos una lógica simple:
		// - Los IDs que empiezan con "CUST" son customers
		// - Los IDs que empiezan con "EMP" son employees

		if (userId.startsWith("CUST")) {
			return AddressEntity.UserType.CUSTOMER;
		} else if (userId.startsWith("EMP")) {
			return AddressEntity.UserType.EMPLOYEE;
		} else {
			// Por defecto, asumimos CUSTOMER
			return AddressEntity.UserType.CUSTOMER;
		}
	}

	private boolean isFirstAddress(String userId) {
		return addressRepository.countByUserIdAndActiveTrue(userId) == 0;
	}

	private void handleDefaultAddress(AddressEntity newDefaultAddress) {
		addressRepository.resetDefaultAddressForUser(newDefaultAddress.getUserId());
		newDefaultAddress.setIsDefault(true);
	}

	private AddressEntity buildAddressEntity(String userId, AddressEntity.UserType userType, AddressRequest request) {
		return AddressEntity.builder()
				.userId(userId)
				.userType(userType)
				.street(request.street().trim())
				.city(request.city().trim())
				.state(request.state().trim())
				.country(request.country().toUpperCase())
				.postalCode(request.postalCode().trim())
				.additionalDetails(request.additionalDetails() != null ? request.additionalDetails().trim() : null)
				.isDefault(request.isDefault() != null && request.isDefault())
				.active(true)
				.build();
	}

	private void updateAddressEntity(AddressEntity entity, AddressRequest request) {
		entity.setStreet(request.street().trim());
		entity.setCity(request.city().trim());
		entity.setState(request.state().trim());
		entity.setCountry(request.country().toUpperCase());
		entity.setPostalCode(request.postalCode().trim());
		entity.setAdditionalDetails(request.additionalDetails() != null ? request.additionalDetails().trim() : null);
		// Note: isDefault managed separately.
	}

	private void softDeleteAddress(AddressEntity entity) {
		entity.setActive(false);
		addressRepository.save(entity);
	}


	private AddressResponse mapToResponse(AddressEntity entity) {
		return new AddressResponse(
				entity.getId() != null ? entity.getId().toString() : null,
				entity.getUserId(),
				entity.getStreet(),
				entity.getCity(),
				entity.getState(),
				entity.getCountry(),
				entity.getPostalCode(),
				entity.getAdditionalDetails(),
				entity.getIsDefault(),
				entity.getCreatedAt(),
				entity.getUpdatedAt()
		);
	}

	private AddressSummaryResponse mapToSummaryResponse(AddressEntity entity) {
		String shortStreet = entity.getStreet().length() > 50 ?
				entity.getStreet().substring(0, 47) + "..." :
				entity.getStreet();

		return new AddressSummaryResponse(
				entity.getId() != null ? entity.getId().toString() : null,
				shortStreet,
				entity.getCity(),
				entity.getCountry(),
				entity.getIsDefault()
		);
	}

	private UUID parseUUID(String id) {
		try {
			return UUID.fromString(id);
		} catch (IllegalArgumentException e) {
			throw new AddressException("Invalid address ID format: " + id);
		}
	}
}