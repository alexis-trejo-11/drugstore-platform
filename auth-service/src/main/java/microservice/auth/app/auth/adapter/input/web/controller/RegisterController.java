package microservice.auth.app.auth.adapter.input.web.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.response.ResponseWrapper;
import microservice.auth.app.auth.adapter.input.web.dto.input.SignupRequest;
import microservice.auth.app.auth.adapter.input.web.dto.response.SignUpResponse;
import microservice.auth.app.auth.core.application.command.SignupCommand;
import microservice.auth.app.auth.core.application.result.SignUpResult;
import microservice.auth.app.auth.core.domain.valueobjects.UserRole;
import microservice.auth.app.auth.core.ports.input.AuthUseCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v2/auth")
public class RegisterController {
	private final AuthUseCases authUseCases;

	@Autowired
	public RegisterController(AuthUseCases authUseCases) {
		this.authUseCases = authUseCases;
	}

	@PostMapping("/register/customer")
	public ResponseEntity<ResponseWrapper<SignUpResponse>> registerCustomer(
			@RequestBody @Valid @NotNull SignupRequest request) {
		SignupCommand command = request.toCommand(UserRole.CUSTOMER);
		SignUpResult result = authUseCases.signUp(command);
		SignUpResponse response = SignUpResponse.fromResult(result);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseWrapper.created(response, "Customer User"));
	}

	@PostMapping("/register/employee")
	public ResponseEntity<ResponseWrapper<SignUpResponse>> registerEmployee(
			@RequestBody @Valid @NotNull SignupRequest request) {
		SignupCommand command = request.toCommand(UserRole.EMPLOYEE);
		SignUpResult result = authUseCases.signUp(command);
		SignUpResponse response = SignUpResponse.fromResult(result);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseWrapper.created(response, "Employee User"));
	}

	@PostMapping("/register/admin")
	public ResponseEntity<ResponseWrapper<SignUpResponse>> registerAdmin(
			@RequestBody @Valid @NotNull SignupRequest request) {
		SignupCommand command = request.toCommand(UserRole.ADMIN);
		SignUpResult result = authUseCases.signUp(command);
		SignUpResponse response = SignUpResponse.fromResult(result);

		return ResponseEntity.status(HttpStatus.CREATED)
				.body(ResponseWrapper.created(response, "Admin User"));
	}
}