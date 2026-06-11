package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.annotations;

import io.github.alexisTrejo11.drugstore.accounts.config.openapi.AuthOpenApiModels;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Operation(
    operationId = "login",
    summary = "Login (email/phone + password)",
    description =
        "Authenticates the user. If the account has TOTP enabled, `requiresTwoFactor` is true "
            + "and you must call `/api/v2/auth/login/2fa` with the emailed identifier and code "
            + "before tokens are issued.")
@ApiResponses({
  @ApiResponse(
      responseCode = "200",
      description =
          "Credentials accepted (full session) or 2FA challenge pending — inspect `data.requiresTwoFactor`.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AuthOpenApiModels.Session.class))),
  @ApiResponse(
      responseCode = "400",
      description = "Validation or bad credentials / domain error (`error.errorCode`).",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AuthOpenApiModels.ErrorPayload.class))),
  @ApiResponse(
      responseCode = "429",
      description = "Sensitive route rate limit exceeded.",
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
public @interface LoginOperation {}
