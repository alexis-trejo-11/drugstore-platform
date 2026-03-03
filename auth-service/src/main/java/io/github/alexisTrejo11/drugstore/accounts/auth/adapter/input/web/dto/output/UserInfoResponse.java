package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.input.web.dto.output;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.github.alexisTrejo11.drugstore.accounts.User;

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

	public static UserInfoResponse fromResult(User result) {
		return UserInfoResponse.builder()
				.userId(result.getId() != null ? result.getId().value() : null)
				.email(result.getEmail() != null ? result.getEmail().value() : null)
				.firstName(result.getFirstName())
				.lastName(result.getLastName())
				.role(result.getRole() != null ? result.getRole().name() : null)
				.twoFactorEnabled(result.isTwoFactorEnabled())
				.build();
	}
}