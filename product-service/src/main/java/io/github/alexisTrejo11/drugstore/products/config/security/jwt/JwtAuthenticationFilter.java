package io.github.alexisTrejo11.drugstore.products.config.security.jwt;

import io.github.alexisTrejo11.drugstore.products.config.security.ApiSecurityResponseWriter;
import io.jsonwebtoken.Claims;
import libs_kernel.security.dto.AuthUserDetails;
import libs_kernel.security.dto.TokenValidationResponse;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenValidator jwtTokenService;
	private final ApiSecurityResponseWriter securityResponseWriter;
	private static final Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	public JwtAuthenticationFilter(
			JwtTokenValidator jwtTokenService,
			ApiSecurityResponseWriter securityResponseWriter) {
		this.jwtTokenService = jwtTokenService;
		this.securityResponseWriter = securityResponseWriter;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {

		String path = request.getRequestURI();
		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			log.debug("No Bearer token present for path: {}, continuing unauthenticated", path);
			filterChain.doFilter(request, response);
			return;
		}

		String token = authHeader.substring(7).trim();

		if (token.isEmpty()) {
			log.debug("Bearer header present but token is empty for path: {}, continuing unauthenticated", path);
			filterChain.doFilter(request, response);
			return;
		}

		try {
			TokenValidationResponse validation = jwtTokenService.validateAccessToken(token);

			if (!validation.isValid()) {
				sendUnauthorizedError(response, validation.message());
				return;
			}

			Claims claims = jwtTokenService.extractAllClaims(token);
			AuthUserDetails userDetails = createUserDetails(claims, validation);

			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + validation.role()))
					);

			SecurityContextHolder.getContext().setAuthentication(authentication);

			request.setAttribute("userId", validation.userId());
			request.setAttribute("userRole", validation.role());
			request.setAttribute("userDetails", userDetails);

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Error processing JWT token", e);
			sendUnauthorizedError(response, "Invalid token: " + e.getMessage());
		}
	}

	private AuthUserDetails createUserDetails(Claims claims, TokenValidationResponse validation) {
		return AuthUserDetails.builder()
				.userId(validation.userId())
				.email(claims.get("email", String.class))
				.role(validation.role())
				.token(claims.getId())
				.build();
	}

	private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
		securityResponseWriter.writeUnauthorized(response, "Unauthorized", message);
	}
}
