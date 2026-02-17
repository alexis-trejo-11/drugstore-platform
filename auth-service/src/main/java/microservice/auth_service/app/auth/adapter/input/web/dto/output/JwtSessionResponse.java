package microservice.auth_service.app.auth.adapter.input.web.dto.output;

public record JwtSessionResponse(
		String accessToken,
		String refreshToken,
		Long accessTokenExpiresIn,
		Long refreshTokenExpiresIn,
		String tokenType,
		String userId
) {
}
