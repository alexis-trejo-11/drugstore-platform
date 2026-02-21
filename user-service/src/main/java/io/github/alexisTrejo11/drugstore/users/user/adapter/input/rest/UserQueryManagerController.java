package io.github.alexisTrejo11.drugstore.users.user.adapter.input.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.GetUserByEmailOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.GetUserByIdOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.GetUserByPhoneOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.GetUsersByRoleOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.annotation.GetUsersByStatusOperation;
import io.github.alexisTrejo11.drugstore.users.user.adapter.input.rest.dto.UserHTTPResponse;
import io.github.alexisTrejo11.drugstore.users.user.core.application.queries.GetUserByEmailQuery;
import io.github.alexisTrejo11.drugstore.users.user.core.application.queries.GetUserByIdQuery;
import io.github.alexisTrejo11.drugstore.users.user.core.application.queries.GetUserByPhoneNumberQuery;
import io.github.alexisTrejo11.drugstore.users.user.core.application.queries.GetUserByStatusQuery;
import io.github.alexisTrejo11.drugstore.users.user.core.application.queries.GetUsersByRoleQuery;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserStatus;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.QueryBus;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;

@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "User Query", description = "Endpoints for querying and retrieving user information")
public class UserQueryManagerController {
  private final QueryBus queryBus;

  @Autowired
  public UserQueryManagerController(QueryBus queryBus) {
    this.queryBus = queryBus;
  }

  @GetUserByIdOperation
  @GetMapping("/{id}")
  public ResponseWrapper<UserHTTPResponse> getUserById(
      @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000") @Valid @PathVariable String id) {
    GetUserByIdQuery query = GetUserByIdQuery.of(id);

    UserQueryResult userQueryResult = queryBus.execute(query);

    var userResponse = UserHTTPResponse.from(userQueryResult);
    return ResponseWrapper.success(userResponse, "User retrieved successfully");
  }

  @GetUserByEmailOperation
  @GetMapping("/by-email/{email}")
  public ResponseWrapper<UserHTTPResponse> getUserByEmail(
      @Parameter(description = "User's email address", required = true, example = "john.doe@example.com") @Valid @PathVariable @NotNull String email) {
    GetUserByEmailQuery query = GetUserByEmailQuery.of(email);

    UserQueryResult queryResult = queryBus.execute(query);

    var userResponse = UserHTTPResponse.from(queryResult);
    return ResponseWrapper.success(userResponse, "User retrieved successfully");
  }

  @GetUserByPhoneOperation
  @GetMapping("by-phone/{phone}")
  public ResponseWrapper<UserHTTPResponse> getUserByPhone(
      @Parameter(description = "User's phone number with country code", required = true, example = "+1-555-123-4567") @Valid @PathVariable @NotNull String phone) {
    GetUserByPhoneNumberQuery query = new GetUserByPhoneNumberQuery(PhoneNumber.of(phone));

    UserQueryResult userResponse = queryBus.execute(query);

    var userResponser = UserHTTPResponse.from(userResponse);
    return ResponseWrapper.success(userResponser, "User retrieved successfully");
  }

  @GetUsersByRoleOperation
  @GetMapping("by-role/{role}")
  public ResponseWrapper<PageResponse<UserHTTPResponse>> getUserByRole(
      @Parameter(description = "User role filter", required = true, example = "CUSTOMER", schema = @Schema(allowableValues = {
          "CUSTOMER", "EMPLOYEE", "ADMIN" })) @PathVariable UserRole role,
      @Parameter(description = "Pagination parameters (page=0, size=10, sort=createdAt,desc)", example = "page=0&size=10") @ModelAttribute PageRequest pagination) {

    GetUsersByRoleQuery query = new GetUsersByRoleQuery(role, pagination.toPageable());
    Page<UserQueryResult> queryResultPage = queryBus.execute(query);
    PageResponse<UserHTTPResponse> pageResponse = PageResponse.from(queryResultPage.map(UserHTTPResponse::from));

    return ResponseWrapper.success(pageResponse, "Users retrieved successfully");
  }

  @GetUsersByStatusOperation
  @GetMapping("by-status/{status}")
  public ResponseWrapper<PageResponse<UserHTTPResponse>> getUserByStatus(
      @Parameter(description = "User status filter", required = true, example = "ACTIVE", schema = @Schema(allowableValues = {
          "PENDING", "ACTIVE", "INACTIVE", "SUSPENDED", "DELETED" })) @PathVariable UserStatus status,
      @Parameter(description = "Pagination parameters (page=0, size=10, sort=createdAt,desc)", example = "page=0&size=10") @ModelAttribute PageRequest pagination) {
    if (pagination == null) {
      pagination = PageRequest.defaultPageRequest();
    }
    GetUserByStatusQuery query = new GetUserByStatusQuery(status, pagination.toPageable());

    Page<UserQueryResult> queryResultPage = queryBus.execute(query);

    PageResponse<UserHTTPResponse> pageResponse = PageResponse.from(queryResultPage.map(UserHTTPResponse::from));
    return ResponseWrapper.success(pageResponse, "Users retrieved successfully");
  }

}
