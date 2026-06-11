package io.github.alexisTrejo11.drugstore.employees.config.security.jwt;


import io.github.alexisTrejo11.drugstore.employees.config.AuthUserDetailsLocal;
import io.github.alexisTrejo11.drugstore.employees.config.security.auth.ApiSecurityResponseWriter;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import libs_kernel.security.dto.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenValidator jwtTokenService;
	private final ApiSecurityResponseWriter securityResponseWriter;
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

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
			AuthUserDetailsLocal userDetails = createUserDetails(claims, validation);

			UsernamePasswordAuthenticationToken authentication =
					new UsernamePasswordAuthenticationToken(
						userDetails,
						null,
						userDetails.getAuthorities()
					);

			SecurityContextHolder.getContext().setAuthentication(authentication);

			// Alternatively, can retrieve user data in request attributes for controllers to use
			request.setAttribute("userId", validation.userId());
			request.setAttribute("userRole", validation.role());
			request.setAttribute("userDetails", userDetails);

			filterChain.doFilter(request, response);
		} catch (Exception e) {
			log.error("Error processing JWT token", e);
			sendUnauthorizedError(response, "Invalid token: " + e.getMessage());
		}
	}

	private AuthUserDetailsLocal createUserDetails(Claims claims, TokenValidationResponse validation) {
		return new AuthUserDetailsLocal(
				validation.userId(),
				claims.get("email", String.class),
				validation.role(),
				claims.getId()
		);
	}


	private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
		securityResponseWriter.writeUnauthorized(response, "Unauthorized", message);
	}
}