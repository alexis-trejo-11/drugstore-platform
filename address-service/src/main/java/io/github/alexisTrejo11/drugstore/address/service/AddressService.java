package io.github.alexisTrejo11.drugstore.address.service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.github.alexisTrejo11.drugstore.address.config.AddressServiceProperties;
import io.github.alexisTrejo11.drugstore.address.entity.AddressEntity;
import io.github.alexisTrejo11.drugstore.address.repository.AddressRepository;
import io.github.alexisTrejo11.drugstore.address.utils.dto.Address;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressRequest;
import io.github.alexisTrejo11.drugstore.address.utils.dto.AddressSummary;
import io.github.alexisTrejo11.drugstore.address.utils.exceptions.AddressException;
import io.github.alexisTrejo11.drugstore.address.utils.exceptions.AddressLimitExceededException;
import io.github.alexisTrejo11.drugstore.address.utils.exceptions.AddressNotFoundException;
import io.github.alexisTrejo11.drugstore.address.utils.exceptions.UnauthorizedAddressAccessException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Service layer for address management operations.
 * Orchestrates business logic while delegating validation and mapping to
 * specialized components.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class AddressService {

  private final AddressRepository addressRepository;
  private final AddressValidator addressValidator;
  private final AddressMapper addressMapper;
  private final AddressServiceProperties properties;

  @Transactional(readOnly = true)
  public Address findAddressById(String addressId) {
    AddressEntity entity = findActiveAddressById(addressId);
    return Address.fromEntity(entity);
  }

  @Transactional(readOnly = true)
  public Address findAddressByIdAndUserId(String addressId, String userId) {
    UUID id = parseUUID(addressId);

    AddressEntity entity = addressRepository.findByIdAndUserIdAndActiveTrue(id, userId)
        .orElseThrow(() -> new AddressNotFoundException(addressId, userId));
    return Address.fromEntity(entity);
  }

  @Transactional(readOnly = true)
  public List<Address> findAddressesByUserId(String userId) {
    return addressRepository.findByUserIdAndActiveTrue(userId).stream()
        .map(Address::fromEntity)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public List<AddressSummary> findAddressSummariesByUserId(String userId) {
    return addressRepository.findByUserIdAndActiveTrue(userId).stream()
        .map(addressMapper::toSummary)
        .collect(Collectors.toList());
  }

  @Transactional(readOnly = true)
  public Page<AddressSummary> findAllAddresses(Pageable pageable) {
    return addressRepository.findByActiveTrue(pageable)
        .map(addressMapper::toSummary);
  }

  @Transactional
  public Address createAddress(String userId, String role, AddressRequest request) {
    addressValidator.validate(request);

    AddressEntity.UserType userType = determineUserType(role);
    validateAddressLimit(userId, userType);

    AddressEntity entity = addressMapper.toEntity(userId, userType, request);

    if (Boolean.TRUE.equals(request.isDefault()) || isFirstAddress(userId)) {
      setAsDefaultAddress(entity);
    }

    AddressEntity savedEntity = addressRepository.save(entity);
    log.info("Address created successfully for user: {}, addressId: {}", userId, savedEntity.getId());

    return Address.fromEntity(savedEntity);
  }

  @Transactional
  public Address updateAddress(String addressId, AddressRequest request) {
    return updateAddress(addressId, request, null);
  }

  @Transactional
  public Address updateAddress(String addressId, AddressRequest request, String userId) {
    addressValidator.validate(request);

    UUID id = parseUUID(addressId);
    AddressEntity entity = findAddressEntityForUpdate(id, userId);

    addressMapper.updateEntity(entity, request);

    if (Boolean.TRUE.equals(request.isDefault()) && !entity.getIsDefault()) {
      setAsDefaultAddress(entity);
    }

    AddressEntity updatedEntity = addressRepository.save(entity);
    log.info("Address updated successfully: {}", addressId);

    return Address.fromEntity(updatedEntity);
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
  public Address setAddressAsDefault(String addressId, String userId) {
    UUID uuid = parseUUID(addressId);
    AddressEntity entity = addressRepository.findByIdAndUserIdAndActiveTrue(uuid, userId)
        .orElseThrow(() -> new AddressNotFoundException(addressId, userId));

    setAsDefaultAddress(entity);
    AddressEntity updatedEntity = addressRepository.save(entity);

    log.info("Address set as default: {} for user: {}", addressId, userId);
    return Address.fromEntity(updatedEntity);
  }

  // ==================== Private Helper Methods ====================

  // ==================== Private Helper Methods ====================

  /**
   * Finds an active address by ID (admin access)
   */
  private AddressEntity findActiveAddressById(String addressId) {
    UUID uuid = parseUUID(addressId);
    return addressRepository.findByIdAndActiveTrue(uuid)
        .orElseThrow(() -> new AddressNotFoundException(addressId));
  }

  /**
   * Finds an address entity for update operation
   * If userId is provided, validates ownership; otherwise allows admin access
   */
  private AddressEntity findAddressEntityForUpdate(UUID id, String userId) {
    if (userId != null) {
      return addressRepository.findByIdAndUserIdAndActiveTrue(id, userId)
          .orElseThrow(() -> new UnauthorizedAddressAccessException(id.toString(), userId));
    }
    return addressRepository.findByIdAndActiveTrue(id)
        .orElseThrow(() -> new AddressNotFoundException(id.toString()));
  }

  /**
   * Validates that user hasn't exceeded their address limit
   */
  private void validateAddressLimit(String userId, AddressEntity.UserType userType) {
    long currentAddressCount = addressRepository.countByUserIdAndActiveTrue(userId);
    int limit = properties.getAddressLimit(userType.name());

    if (currentAddressCount >= limit) {
      throw new AddressLimitExceededException(userId, limit, userType.name());
    }
  }

  /**
   * Determines user type from role string
   */
  private AddressEntity.UserType determineUserType(String role) {
    if (role.contains("CUSTOMER")) {
      return AddressEntity.UserType.CUSTOMER;
    } else if (role.contains("EMPLOYEE")) {
      return AddressEntity.UserType.EMPLOYEE;
    }
    return AddressEntity.UserType.CUSTOMER; // Default to CUSTOMER
  }

  /**
   * Checks if this is the user's first address
   */
  private boolean isFirstAddress(String userId) {
    return addressRepository.countByUserIdAndActiveTrue(userId) == 0;
  }

  /**
   * Sets an address as the default for the user
   * Resets any existing default address first
   */
  private void setAsDefaultAddress(AddressEntity newDefaultAddress) {
    addressRepository.resetDefaultAddressForUser(newDefaultAddress.getUserId());
    newDefaultAddress.setIsDefault(true);
  }

  /**
   * Performs soft delete on an address
   */
  private void softDeleteAddress(AddressEntity entity) {
    entity.setActive(false);
    addressRepository.save(entity);
  }

  /**
   * Parses a string to UUID
   * 
   * @throws AddressException if the ID format is invalid
   */
  private UUID parseUUID(String id) {
    try {
      return UUID.fromString(id);
    } catch (IllegalArgumentException e) {
      throw new AddressException("Invalid address ID format: " + id);
    }
  }
}