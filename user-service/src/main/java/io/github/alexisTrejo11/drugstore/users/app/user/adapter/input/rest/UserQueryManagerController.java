package io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import libs_kernel.page.PageRequest;
import libs_kernel.page.PageResponse;
import libs_kernel.response.ResponseWrapper;
import io.github.alexisTrejo11.drugstore.users.app.user.adapter.input.rest.dto.UserHTTPResponse;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.GetUserByEmailQuery;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.GetUserByIdQuery;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.GetUserByPhoneNumberQuery;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.GetUserByStatusQuery;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.queries.GetUsersByRoleQuery;
import io.github.alexisTrejo11.drugstore.users.app.user.core.application.result.UserQueryResult;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.enums.UserStatus;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.PhoneNumber;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.input.QueryBus;

@RestController
@RequestMapping("/api/v2/users")
@Tag(name = "User Query", description = "Endpoints for querying and retrieving user information")
public class UserQueryManagerController {
  private final QueryBus queryBus;

  @Autowired
  public UserQueryManagerController(QueryBus queryBus) {
    this.queryBus = queryBus;
  }

  @Operation(
      summary = "Get user by ID",
      description = "Retrieves detailed information about a specific user by their unique identifier (UUID).",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User found and retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseWrapper.class),
              examples = @ExampleObject(
                  name = "Success",
                  value = """
                      {
                        "message": "User retrieved successfully",
                        "data": {
                          "id": "550e8400-e29b-41d4-a716-446655440000",
                          "email": "john.doe@example.com",
                          "phoneNumber": "+1-555-123-4567",
                          "role": "CUSTOMER",
                          "joinedAt": "2026-01-15T10:30:00",
                          "lastLoginAt": "2026-02-19T09:15:00"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad request - Invalid user ID format",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = """
                      {
                        "message": "Invalid user ID format",
                        "error": {
                          "code": "INVALID_FORMAT",
                          "details": "User ID must be a valid UUID"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "422",
          description = "Unprocessable Entity - User not found",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = """
                      {
                        "message": "User not found",
                        "error": {
                          "code": "USER_NOT_FOUND",
                          "details": "No user exists with the provided ID"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(mediaType = "application/json")
      )
  })
  @GetMapping("/{id}")
  public ResponseWrapper<UserHTTPResponse> getUserById(
      @Parameter(description = "User ID (UUID format)", required = true, example = "550e8400-e29b-41d4-a716-446655440000")
      @Valid @PathVariable String id) {
    GetUserByIdQuery query = GetUserByIdQuery.of(id);

    UserQueryResult userQueryResult = queryBus.execute(query);

    var userResponse = UserHTTPResponse.from(userQueryResult);
    return ResponseWrapper.success(userResponse, "User retrieved successfully");
  }

  @Operation(
      summary = "Get user by email",
      description = "Retrieves user information by their email address. Email lookup is case-insensitive.",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User found and retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseWrapper.class),
              examples = @ExampleObject(
                  name = "Success",
                  value = """
                      {
                        "message": "User retrieved successfully",
                        "data": {
                          "id": "550e8400-e29b-41d4-a716-446655440000",
                          "email": "john.doe@example.com",
                          "phoneNumber": "+1-555-123-4567",
                          "role": "CUSTOMER",
                          "joinedAt": "2026-01-15T10:30:00",
                          "lastLoginAt": "2026-02-19T09:15:00"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad request - Invalid email format",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = """
                      {
                        "message": "Invalid email format",
                        "error": {
                          "code": "INVALID_EMAIL",
                          "details": "Email must be a valid email address"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "422",
          description = "Unprocessable Entity - User with email not found",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = """
                      {
                        "message": "User not found",
                        "error": {
                          "code": "USER_NOT_FOUND",
                          "details": "No user exists with email john.doe@example.com"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(mediaType = "application/json")
      )
  })
  @GetMapping("/by-email/{email}")
  public ResponseWrapper<UserHTTPResponse> getUserByEmail(
      @Parameter(description = "User's email address", required = true, example = "john.doe@example.com")
      @Valid @PathVariable @NotNull String email) {
    GetUserByEmailQuery query = GetUserByEmailQuery.of(email);

    UserQueryResult queryResult = queryBus.execute(query);

    var userResponse = UserHTTPResponse.from(queryResult);
    return ResponseWrapper.success(userResponse, "User retrieved successfully");
  }

  @Operation(
      summary = "Get user by phone number",
      description = "Retrieves user information by their phone number. Phone number must include country code (e.g., +1-555-123-4567).",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "User found and retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseWrapper.class),
              examples = @ExampleObject(
                  name = "Success",
                  value = """
                      {
                        "message": "User retrieved successfully",
                        "data": {
                          "id": "550e8400-e29b-41d4-a716-446655440000",
                          "email": "john.doe@example.com",
                          "phoneNumber": "+1-555-123-4567",
                          "role": "CUSTOMER",
                          "joinedAt": "2026-01-15T10:30:00",
                          "lastLoginAt": "2026-02-19T09:15:00"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad request - Invalid phone number format",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = """
                      {
                        "message": "Invalid phone number format",
                        "error": {
                          "code": "INVALID_PHONE",
                          "details": "Phone number must include country code and be properly formatted"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "422",
          description = "Unprocessable Entity - User with phone number not found",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = """
                      {
                        "message": "User not found",
                        "error": {
                          "code": "USER_NOT_FOUND",
                          "details": "No user exists with phone number +1-555-123-4567"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(mediaType = "application/json")
      )
  })
  @GetMapping("by-phone/{phone}")
  public ResponseWrapper<UserHTTPResponse> getUserByPhone(
      @Parameter(description = "User's phone number with country code", required = true, example = "+1-555-123-4567")
      @Valid @PathVariable @NotNull String phone) {
    GetUserByPhoneNumberQuery query = new GetUserByPhoneNumberQuery(PhoneNumber.of(phone));

    UserQueryResult userResponse = queryBus.execute(query);

    var userResponser = UserHTTPResponse.from(userResponse);
    return ResponseWrapper.success(userResponser, "User retrieved successfully");
  }

  @Operation(
      summary = "Get users by role (paginated)",
      description = "Retrieves all users with a specific role with pagination support. Available roles: CUSTOMER, EMPLOYEE, ADMIN.",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Users retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseWrapper.class),
              examples = @ExampleObject(
                  name = "Success with pagination",
                  value = """
                      {
                        "message": "Users retrieved successfully",
                        "data": {
                          "content": [
                            {
                              "id": "550e8400-e29b-41d4-a716-446655440000",
                              "email": "john.doe@example.com",
                              "phoneNumber": "+1-555-123-4567",
                              "role": "CUSTOMER",
                              "joinedAt": "2026-01-15T10:30:00",
                              "lastLoginAt": "2026-02-19T09:15:00"
                            },
                            {
                              "id": "660e8400-e29b-41d4-a716-446655440001",
                              "email": "jane.smith@example.com",
                              "phoneNumber": "+1-555-987-6543",
                              "role": "CUSTOMER",
                              "joinedAt": "2026-01-20T14:22:00",
                              "lastLoginAt": "2026-02-18T16:45:00"
                            }
                          ],
                          "pagination_metadata": {
                            "totalElements": 150,
                            "totalPages": 15,
                            "currentPage": 0,
                            "pageSize": 10,
                            "hasNext": true,
                            "hasPrevious": false
                          }
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad request - Invalid role or pagination parameters",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = """
                      {
                        "message": "Invalid role",
                        "error": {
                          "code": "INVALID_ROLE",
                          "details": "Role must be one of: CUSTOMER, EMPLOYEE, ADMIN"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(mediaType = "application/json")
      )
  })
  @GetMapping("by-role/{role}")
  public ResponseWrapper<PageResponse<UserHTTPResponse>> getUserByRole(
      @Parameter(description = "User role filter", required = true, example = "CUSTOMER", schema = @Schema(allowableValues = {"CUSTOMER", "EMPLOYEE", "ADMIN"}))
      @PathVariable UserRole role,
      @Parameter(description = "Pagination parameters (page=0, size=10, sort=createdAt,desc)", example = "page=0&size=10")
      @ModelAttribute PageRequest pagination) {

    GetUsersByRoleQuery query = new GetUsersByRoleQuery(role, pagination.toPageable());
    Page<UserQueryResult> queryResultPage = queryBus.execute(query);
    PageResponse<UserHTTPResponse> pageResponse = PageResponse.from(queryResultPage.map(UserHTTPResponse::from));

    return ResponseWrapper.success(pageResponse, "Users retrieved successfully");
  }

  @Operation(
      summary = "Get users by status (paginated)",
      description = "Retrieves all users with a specific status with pagination support. Available statuses: PENDING, ACTIVE, INACTIVE, SUSPENDED, DELETED.",
      security = @SecurityRequirement(name = "bearerAuth")
  )
  @ApiResponses(value = {
      @ApiResponse(
          responseCode = "200",
          description = "Users retrieved successfully",
          content = @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = ResponseWrapper.class),
              examples = @ExampleObject(
                  name = "Success with pagination",
                  value = """
                      {
                        "message": "Users retrieved successfully",
                        "data": {
                          "content": [
                            {
                              "id": "550e8400-e29b-41d4-a716-446655440000",
                              "email": "john.doe@example.com",
                              "phoneNumber": "+1-555-123-4567",
                              "role": "CUSTOMER",
                              "joinedAt": "2026-01-15T10:30:00",
                              "lastLoginAt": "2026-02-19T09:15:00"
                            },
                            {
                              "id": "660e8400-e29b-41d4-a716-446655440001",
                              "email": "jane.smith@example.com",
                              "phoneNumber": "+1-555-987-6543",
                              "role": "EMPLOYEE",
                              "joinedAt": "2026-01-20T14:22:00",
                              "lastLoginAt": "2026-02-18T16:45:00"
                            }
                          ],
                          "pagination_metadata": {
                            "totalElements": 245,
                            "totalPages": 25,
                            "currentPage": 0,
                            "pageSize": 10,
                            "hasNext": true,
                            "hasPrevious": false
                          }
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "400",
          description = "Bad request - Invalid status or pagination parameters",
          content = @Content(
              mediaType = "application/json",
              examples = @ExampleObject(
                  value = """
                      {
                        "message": "Invalid status",
                        "error": {
                          "code": "INVALID_STATUS",
                          "details": "Status must be one of: PENDING, ACTIVE, INACTIVE, SUSPENDED, DELETED"
                        },
                        "timestamp": "2026-02-19T10:30:00"
                      }
                      """
              )
          )
      ),
      @ApiResponse(
          responseCode = "500",
          description = "Internal server error",
          content = @Content(mediaType = "application/json")
      )
  })
  @GetMapping("by-status/{status}")
  public ResponseWrapper<PageResponse<UserHTTPResponse>> getUserByStatus(
      @Parameter(description = "User status filter", required = true, example = "ACTIVE", schema = @Schema(allowableValues = {"PENDING", "ACTIVE", "INACTIVE", "SUSPENDED", "DELETED"}))
      @PathVariable UserStatus status,
      @Parameter(description = "Pagination parameters (page=0, size=10, sort=createdAt,desc)", example = "page=0&size=10")
      @ModelAttribute PageRequest pagination) {
    if (pagination == null) {
      pagination = PageRequest.defaultPageRequest();
    }
    GetUserByStatusQuery query = new GetUserByStatusQuery(status, pagination.toPageable());

    Page<UserQueryResult> queryResultPage = queryBus.execute(query);

    PageResponse<UserHTTPResponse> pageResponse = PageResponse.from(queryResultPage.map(UserHTTPResponse::from));
    return ResponseWrapper.success(pageResponse, "Users retrieved successfully");
  }

}
