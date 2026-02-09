package libs_kernel.security.jwt;

import io.jsonwebtoken.Claims;
import libs_kernel.security.dto.AuthUserDetails;
import libs_kernel.security.dto.TokenValidationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenManager jwtTokenService;
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	public JwtAuthenticationFilter(JwtTokenManager jwtTokenService) {
		this.jwtTokenService = jwtTokenService;
	}

	private static final List<String> PUBLIC_ENDPOINTS = List.of(
			"/api/v2/auth/login",
			"/api/v2/auth/products",
			"/api/v2/auth/stores",
			"/api/v2/auth/refresh",
			"/api/v2/auth/validate",
			"/api/v2/stores/**",
			"/v3/api-docs/**",
			"/swagger-ui/**",
			"/swagger-ui.html",
			"/actuator/health",
			"/actuator/info",
			"/error"
	);

	@Override
	protected void doFilterInternal(HttpServletRequest request,
	                                HttpServletResponse response,
	                                FilterChain filterChain) throws ServletException, IOException {

		String path = request.getRequestURI();

		if (isPublicEndpoint(path)) {
			filterChain.doFilter(request, response);
			return;
		}

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			return;
		}

		String token = authHeader.substring(7);

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

	private AuthUserDetails createUserDetails(Claims claims, TokenValidationResponse validation) {
		return AuthUserDetails.builder()
				.userId(validation.userId())
				.role(validation.role())
				.email(claims.get("email", String.class))
				.token(claims.getId())
				.build();
	}

	private boolean isPublicEndpoint(String path) {
		return PUBLIC_ENDPOINTS.stream().anyMatch(pattern ->
				path.equals(pattern) ||
						(pattern.contains("**") && path.startsWith(pattern.replace("**", ""))) ||
						(pattern.endsWith("/**") && path.startsWith(pattern.replace("/**", "")))
		);
	}

	private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write(
				String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\", \"code\": \"UNAUTHORIZED\"}",
						message)
		);
	}
}