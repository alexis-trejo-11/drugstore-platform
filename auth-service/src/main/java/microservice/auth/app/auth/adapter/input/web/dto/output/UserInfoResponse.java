// 2. UserInfoResponse.java (NUEVO)
package microservice.auth.app.auth.adapter.input.web.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.auth.app.auth.core.application.result.UserInfoResult;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserInfoResponse {
    private String userId;
    private String email;
    private String firstName;
    private String lastName;
    private String role;
    private Boolean twoFactorEnabled;

    public static UserInfoResponse fromResult(UserInfoResult result) {
        return UserInfoResponse.builder()
                .userId(result.getUserId())
                .email(result.getEmail())
                .firstName(result.getFirstName())
                .lastName(result.getLastName())
                .role(result.getRole())
                .twoFactorEnabled(result.getTwoFactorEnabled())
                .build();
    }
}