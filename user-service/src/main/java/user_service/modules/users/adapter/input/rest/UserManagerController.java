package user_service.modules.users.adapter.input.rest;

import jakarta.validation.constraints.NotBlank;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.response.ResponseWrapper;
import user_service.modules.users.adapter.input.rest.dto.UserHTTPResponse;
import user_service.modules.users.adapter.input.rest.dto.UserRequest;
import user_service.modules.users.core.application.command.CreateUserCommand;
import user_service.modules.users.core.application.command.DeleteUserCommand;
import user_service.modules.users.core.application.command.UpdateUserStatusCommand;
import user_service.modules.users.core.application.queries.GetUserByEmailQuery;
import user_service.modules.users.core.application.queries.GetUserByIdQuery;
import user_service.modules.users.core.application.queries.GetUserByPhoneNumberQuery;
import user_service.modules.users.core.application.queries.ListUserByRoleQuery;
import user_service.modules.users.core.application.queries.ListUserByStatusQuery;
import user_service.modules.users.core.application.result.CommandResult;

import user_service.modules.users.core.application.result.UserQueryResult;
import user_service.modules.users.core.domain.models.enums.UserRole;
import user_service.modules.users.core.domain.models.enums.UserStatus;
import user_service.modules.users.core.domain.models.valueobjects.PhoneNumber;
import user_service.modules.users.core.ports.input.CommandBus;
import user_service.modules.users.core.ports.input.QueryBus;

@RestController
@RequestMapping("/api/v2/users")
public class UserManagerController {
	private final CommandBus commandBus;
	private final QueryBus queryBus;

	@Autowired
	public UserManagerController(CommandBus commandBus, QueryBus queryBus) {
		this.commandBus = commandBus;
		this.queryBus = queryBus;
	}

	@PostMapping("/")
	public ResponseEntity<ResponseWrapper<?>> createUser(@Valid @RequestBody UserRequest userRequest) {
		CreateUserCommand command = userRequest.toCommand(UserRole.CUSTOMER);

		CommandResult commandResult = commandBus.dispatch(command);

		var response = ResponseWrapper.success(commandResult.data(), "User created successfully");
		return ResponseEntity.status(201).body(response);
	}

	@PatchMapping("/{id}/ban")
	public ResponseWrapper<Void> banUser(@PathVariable String id) {
		UpdateUserStatusCommand command = UpdateUserStatusCommand.ban(id);

		CommandResult result = commandBus.dispatch(command);
		if (!result.success()) {
			throw new IllegalArgumentException("Failed to ban user: " + result.message());
		}

		return ResponseWrapper.success("User banned successfully");
	}

	@PatchMapping("/{id}/unban")
	public ResponseWrapper<Void> unbanUser(@PathVariable String id) {
		UpdateUserStatusCommand command = UpdateUserStatusCommand.unban(id);

		CommandResult result = commandBus.dispatch(command);
		if (!result.success()) {
			throw new IllegalArgumentException("Failed to unban user: " + result.message());
		}

		return ResponseWrapper.success("User unbanned successfully");
	}

	@PatchMapping("/{id}/activate/code/{activationCode}")
	public ResponseWrapper<Void> activateUser(@PathVariable @Valid @NotBlank String id,
	                                          @PathVariable @NotBlank String activationCode) {
		UpdateUserStatusCommand command = UpdateUserStatusCommand.activate(id, activationCode);

		CommandResult result = commandBus.dispatch(command);
		if (!result.success()) {
			throw new IllegalArgumentException("Failed to activate user: " + result.message());
		}

		return ResponseWrapper.success("User activated successfully");
	}

	@GetMapping("/{id}")
	public ResponseWrapper<UserHTTPResponse> getUserById(@Valid @PathVariable String id) {
		GetUserByIdQuery query = GetUserByIdQuery.of(id);

		UserQueryResult userQueryResult = queryBus.execute(query);

		var userResponse = UserHTTPResponse.from(userQueryResult);
		return ResponseWrapper.success(userResponse, "User retrieved successfully");
	}

	@GetMapping("/by-email/{email}")
	public ResponseEntity<UserHTTPResponse> getUserByEmail(@Valid @PathVariable @NotNull String email) {
		GetUserByEmailQuery query = GetUserByEmailQuery.of(email);

		UserQueryResult queryResult = queryBus.execute(query);

		var userResponse = UserHTTPResponse.from(queryResult);
		return ResponseEntity.ok(userResponse);
	}

	@GetMapping("by-phone/{phone}")
	public ResponseWrapper<UserHTTPResponse> getUserByPhone(@Valid @PathVariable @NotNull String phone) {
		GetUserByPhoneNumberQuery query = new GetUserByPhoneNumberQuery(PhoneNumber.of(phone));

		UserQueryResult userResponse = queryBus.execute(query);

		var userResponser = UserHTTPResponse.from(userResponse);
		return ResponseWrapper.success(userResponser, "User retrieved successfully");
	}

	@GetMapping("by-role/{role}")
	public ResponseWrapper<PageResponse<UserHTTPResponse>> listUserByRole(@PathVariable UserRole role,
	                                                                      @ModelAttribute PageRequest pagination) {

		ListUserByRoleQuery query = new ListUserByRoleQuery(role, pagination.toPageable());
		Page<UserQueryResult> queryResultPage = queryBus.execute(query);
		PageResponse<UserHTTPResponse> pageResponse = PageResponse.from(queryResultPage.map(UserHTTPResponse::from));

		return ResponseWrapper.success(pageResponse, "Users retrieved successfully");
	}

	@GetMapping("by-status/{status}")
	public ResponseWrapper<PageResponse<UserHTTPResponse>> listUserByStatus(
			@PathVariable UserStatus status,
			@ModelAttribute PageRequest pagination) {
		if (pagination == null) {
			pagination = PageRequest.defaultPageRequest();
		}
		ListUserByStatusQuery query = new ListUserByStatusQuery(status, pagination.toPageable());

		Page<UserQueryResult> queryResultPage = queryBus.execute(query);

		PageResponse<UserHTTPResponse> pageResponse = PageResponse.from(queryResultPage.map(UserHTTPResponse::from));
		return ResponseWrapper.success(pageResponse, "Users retrieved successfully");
	}

	@DeleteMapping("/{id}")
	public ResponseWrapper<Void> deleteUser(@PathVariable String id) {
		DeleteUserCommand command = DeleteUserCommand.of(id);
		CommandResult result = commandBus.dispatch(command);
		if (!result.success()) {
			throw new IllegalArgumentException("Failed to delete user: " + result.message());
		}

		return ResponseWrapper.success("User deleted successfully");
	}

}
