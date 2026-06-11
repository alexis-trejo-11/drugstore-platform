package io.github.alexisTrejo11.drugstore.accounts.config.openapi;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output.SessionResponse;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output.SignUpResponse;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.TwoFactorQRResult;
import io.swagger.v3.oas.annotations.media.Schema;
import libs_kernel.documentation.SwaggerResponseWrapper;

/**
 * Concrete {@link SwaggerResponseWrapper} subclasses so SpringDoc can emit schemas for \
 * {@code ResponseWrapper<T>} per payload type.
 */
public final class AuthOpenApiModels {

  private AuthOpenApiModels() {}

  @Schema(
      name = "ApiResponseSessionResponse",
      description = "Successful operation returning session tokens and optional 2FA pending state")
  public static class Session extends SwaggerResponseWrapper<SessionResponse> {}

  @Schema(
      name = "ApiResponseSignUpResponse",
      description = "Successful registration returning created user summary")
  public static class SignUp extends SwaggerResponseWrapper<SignUpResponse> {}

  @Schema(
      name = "ApiResponseTwoFactorQrResult",
      description = "2FA setup: QR URL, secret, and manual entry key")
  public static class TwoFactorQr extends SwaggerResponseWrapper<TwoFactorQRResult> {}

  @Schema(
      name = "ApiResponseVoid",
      description = "Success with no body payload (data may be null)")
  public static class VoidPayload extends SwaggerResponseWrapper<Void> {}

  @Schema(
      name = "ApiResponseError",
      description = "Error or validation failure (check `error` for codes and field details)")
  public static class ErrorPayload extends SwaggerResponseWrapper<Object> {}
}
