package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SessionResult;

import java.time.LocalDateTime;

@Builder
public record SessionResponse(
		TokenResponse accessToken,
		TokenResponse refreshToken,
		String userId
) {
    public static SessionResponse fromResult(SessionResult result) {
        var builder = SessionResponse.builder()
				        .userId(result.userId() != null ? result.userId().value() : null);

				if (result.refreshToken() != null) {
					builder.refreshToken(new TokenResponse(
							result.refreshToken().token(),
							result.refreshToken().type(),
							result.refreshToken().expiresIn().toSeconds(),
							result.refreshToken().expiresAt()
					));
				}
				if (result.accessToken() != null) {
					builder.accessToken(new TokenResponse(
							result.accessToken().token(),
							result.accessToken().type(),
							result.accessToken().expiresIn().toSeconds(),
							result.accessToken().expiresAt()
					));
				}
				return builder.build();

    }

		public record TokenResponse(
				String token,
				String type,
				Long expiresIn,
				LocalDateTime expiresAt
		) {
}






}