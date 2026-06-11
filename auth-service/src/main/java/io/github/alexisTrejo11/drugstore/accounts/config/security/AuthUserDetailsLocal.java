package io.github.alexisTrejo11.drugstore.accounts.config.security;


import lombok.Builder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.Collections;

@Builder
public class AuthUserDetailsLocal implements UserDetails {
	private String userId;
	private String email;
	private String role;
	private String token;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + role));
	}

	public AuthUserDetailsLocal(String userId, String email, String role, String token) {
		this.userId = userId;
		this.email = email;
		this.role = role;
		this.token = token;
	}

	@Override
	public String getPassword() {
		return null;
	}

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

	@Override
	public String getUsername() {
		return userId;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}