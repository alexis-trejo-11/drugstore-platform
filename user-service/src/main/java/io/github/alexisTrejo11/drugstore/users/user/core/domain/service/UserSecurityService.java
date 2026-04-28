package io.github.alexisTrejo11.drugstore.users.user.core.domain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.UserRepository;
import libs_kernel.response.Result;

@Service
public class UserSecurityService {
  private final UserRepository userRepository;

  @Autowired
  public UserSecurityService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public boolean isEmailUnique(Email email) {
    return !userRepository.existsByEmail(email);
  }

  public boolean isPhoneNumberUnique(PhoneNumber phoneNumber) {
    return !userRepository.existsByPhoneNumber(phoneNumber);
  }

  public void validatePasswordStrength(String password) {
    PasswordValidator.validatePasswordStrength(password);
  }

  public Result<Void> validateUserUniqueness(Email email, PhoneNumber phoneNumber) {
    if (!isEmailUnique(email)) {
      return Result.error("Email is already in use.");
    }

    if (!isPhoneNumberUnique(phoneNumber)) {
      return Result.error("Phone number is already in use.");
    }

    return Result.success(null);
  }

  public void processUserRegistration(Email email, PhoneNumber phoneNumber, String password) {
    // Validar la fortaleza de la contraseña
    validatePasswordStrength(password);

    // Validar la unicidad del email y número de teléfono
    Result<Void> uniquenessResult = validateUserUniqueness(email, phoneNumber);
    if (!uniquenessResult.isSuccess()) {
      throw new IllegalArgumentException(uniquenessResult.getMessage());
    }

    // Aquí se podría agregar lógica adicional para el registro del usuario,
    // como encriptar la contraseña antes de guardarla en la base de datos.
  }
}
