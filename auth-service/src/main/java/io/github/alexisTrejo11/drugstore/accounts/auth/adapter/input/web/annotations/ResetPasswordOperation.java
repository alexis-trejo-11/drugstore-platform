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
    operationId = "resetPassword",
    summary = "Reset password with token",
    description = "Sets a new password using the single-use token from email.")
@ApiResponses({
  @ApiResponse(
      responseCode = "200",
      description = "Password updated.",
      content =
          @Content(
              mediaType = "application/json",
              schema = @Schema(implementation = AuthOpenApiModels.VoidPayload.class))),
  @ApiResponse(
      responseCode = "400",
      description = "Invalid token, weak password, or confirmation mismatch.",
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
public @interface ResetPasswordOperation {}
