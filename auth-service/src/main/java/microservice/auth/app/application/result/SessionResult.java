package microservice.auth.app.application.result;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth.app.core.valueobjects.Token;
import microservice.auth.app.core.valueobjects.UserId;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SessionResult {
    private UserId userId;
    private Token jwtRefreshToken;
    private Token jwtAccessToken;
    private String jwtAccessTokenType = "Bearer";
}
