package microservice.auth_service.app.auth.adapter.input.web.controller;

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
import microservice.auth_service.app.auth.adapter.input.web.dto.input.SignupRequest;
import microservice.auth_service.app.auth.adapter.input.web.dto.output.SignUpResponse;
import microservice.auth_service.app.auth.core.application.command.SignupCommand;
import microservice.auth_service.app.auth.core.application.result.SignUpResult;
import microservice.auth_service.app.auth.core.domain.valueobjects.UserRole;
import microservice.auth_service.app.auth.core.ports.input.AuthUseCases;

@RestController
@RequestMapping("/api/v2/auth")
@Slf4j
public class RegisterController {
	private final AuthUseCases authUseCases;

	@Autowired
	public RegisterController(AuthUseCases authUseCases) {
		this.authUseCases = authUseCases;
	}

	@PostMapping("/register/customer")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<SignUpResponse>> registerCustomer(
			@RequestBody @Valid @NotNull SignupRequest request) {

		log.info("Received registration request for email: {}", request.email());
		SignupCommand command = request.toCommand(UserRole.CUSTOMER);
		SignUpResult result = authUseCases.signUp(command);
		SignUpResponse response = SignUpResponse.fromResult(result);

		log.info("Registering Customer User: {}", request.email());
		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseWrapper.created(response, "Customer User"));
	}

	@PostMapping("/register/employee")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<SignUpResponse>> registerEmployee(
			@RequestBody @Valid @NotNull SignupRequest request) {
		SignupCommand command = request.toCommand(UserRole.EMPLOYEE);
		SignUpResult result = authUseCases.signUp(command);
		SignUpResponse response = SignUpResponse.fromResult(result);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseWrapper.created(response, "Employee User"));
	}

	@PostMapping("/register/admin")
	@RateLimit(profile = RateLimitProfile.SENSITIVE)
	public ResponseEntity<ResponseWrapper<SignUpResponse>> registerAdmin(
			@RequestBody @Valid @NotNull SignupRequest request) {
		SignupCommand command = request.toCommand(UserRole.ADMIN);
		SignUpResult result = authUseCases.signUp(command);
		SignUpResponse response = SignUpResponse.fromResult(result);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseWrapper.created(response, "Admin User"));
	}
}