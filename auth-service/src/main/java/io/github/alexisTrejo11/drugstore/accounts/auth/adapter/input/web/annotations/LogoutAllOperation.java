package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations;

import io.github.alexisTrejo11.drugstore.accounts.config.OpenApiConfig;
import io.github.alexisTrejo11.drugstore.accounts.config.openapi.AuthOpenApiModels;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@SecurityRequirement(name = OpenApiConfig.BEARER_SCHEME)
@Operation(
    operationId = "logoutAll",
    summary = "Logout all sessions",
    description =
        "Revokes **all** refresh sessions for the subject inferred from the **Bearer access "
            + "token**. `userId` is resolved server-side from JWT — not sent in the body.")
@ApiResponses({
  @ApiResponse(
      responseCode = "200",
      description = "All refresh tokens for the user invalidated.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AuthOpenApiModels.VoidPayload.class))),
  @ApiResponse(
      responseCode = "401",
      description = "Missing or invalid access token.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AuthOpenApiModels.ErrorPayload.class))),
  @ApiResponse(
      responseCode = "429",
      description = "Rate limit exceeded.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AuthOpenApiModels.ErrorPayload.class))),
  @ApiResponse(
      responseCode = "500",
      description = "Unexpected error.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AuthOpenApiModels.ErrorPayload.class)))
})
public @interface LogoutAllOperation {}
