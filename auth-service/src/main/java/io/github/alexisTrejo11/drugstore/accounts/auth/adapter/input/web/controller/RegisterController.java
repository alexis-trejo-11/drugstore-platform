package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.controller;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.ActivationRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.config.rate_limit.RateLimit;
import libs_kernel.config.rate_limit.RateLimitProfile;
import libs_kernel.response.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.input.SignupRequest;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output.SignUpResponse;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.UserRole;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input.RegisterUseCases;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.ActivateAccountOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.RegisterAdminOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.RegisterCustomerOperation;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations.RegisterEmployeeOperation;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/v2/auth")
@Slf4j
@Tag(
    name = "Registration & activation",
    description =
        "Self-service sign-up by role, plus email activation. All routes use **SENSITIVE** rate limits.")
public class RegisterController {
  private final RegisterUseCases useCases;

  @Autowired
  public RegisterController(RegisterUseCases useCases) {
    this.useCases = useCases;
  }

  @PostMapping("/activate")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @ActivateAccountOperation
  public ResponseEntity<ResponseWrapper<Void>> activateAccount(
      @RequestBody @Valid @NotNull ActivationRequest request) {
    log.info("Account activation request received");
    useCases.activateAccount(request.activationCode());
    return ResponseEntity.ok(ResponseWrapper.success(null, "Account activated successfully"));
  }

  @PostMapping("/register/customer")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @RegisterCustomerOperation
  public ResponseEntity<ResponseWrapper<SignUpResponse>> registerCustomer(
      @RequestBody @Valid @NotNull SignupRequest request) {
    log.info("Received registration request for email: {}", request.email());
    SignupCommand command = request.toCommand(UserRole.CUSTOMER);

    SignUpResult result = useCases.register(command);

    SignUpResponse response = SignUpResponse.fromResult(result);
    log.info("Registering Customer User: {}", request.email());

    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResponseWrapper.created(response, "Customer User"));
  }

  @PostMapping("/register/employee")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @RegisterEmployeeOperation
  public ResponseEntity<ResponseWrapper<SignUpResponse>> registerEmployee(
      @RequestBody @Valid @NotNull SignupRequest request) {
    SignupCommand command = request.toCommand(UserRole.EMPLOYEE);

    SignUpResult result = useCases.register(command);

    SignUpResponse response = SignUpResponse.fromResult(result);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResponseWrapper.created(response, "Employee User"));
  }

  @PostMapping("/register/admin")
  @RateLimit(profile = RateLimitProfile.SENSITIVE)
  @RegisterAdminOperation
  public ResponseEntity<ResponseWrapper<SignUpResponse>> registerAdmin(
      @RequestBody @Valid @NotNull SignupRequest request) {
    SignupCommand command = request.toCommand(UserRole.ADMIN);

    SignUpResult result = useCases.register(command);

    SignUpResponse response = SignUpResponse.fromResult(result);
    return ResponseEntity.status(HttpStatus.CREATED)
        .body(ResponseWrapper.created(response, "Admin User"));
  }
}
