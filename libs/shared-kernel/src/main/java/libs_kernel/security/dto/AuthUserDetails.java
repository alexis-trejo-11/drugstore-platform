package libs_kernel.security.dto;

import lombok.Builder;


import java.util.Collection;
import java.util.Collections;

@Builder
public class AuthUserDetails {
	private String userId;
	private String email;
	private String role;
	private String token;

	public String getUserId() {
		return userId;
	}

	public String getEmail() {
		return email;
	}

	public String getRole() {
		return role;
	}

	public String getToken() {
		return token;
	}

	public boolean isAdmin() {
		return "ADMIN".equalsIgnoreCase(role);
	}

	public boolean isEmployee() {
		return "EMPLOYEE".equalsIgnoreCase(role);
	}

	public boolean isCustomer() {
		return "CUSTOMER".equalsIgnoreCase(role);
	}

	public String getUsername() {
		return userId;
	}

	public boolean isAccountNonExpired() {
		return true;
	}

	public boolean isAccountNonLocked() {
		return true;
	}

	public boolean isCredentialsNonExpired() {
		return true;
	}

	public boolean isEnabled() {
		return true;
	}
}
