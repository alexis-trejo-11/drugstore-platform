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

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
	private final JwtTokenValidator jwtTokenService;
	private static final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	@Autowired
	public JwtAuthenticationFilter(JwtTokenValidator jwtTokenService) {
		this.jwtTokenService = jwtTokenService;
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


	private void sendUnauthorizedError(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType("application/json");
		response.getWriter().write(
				String.format("{\"error\": \"Unauthorized\", \"message\": \"%s\", \"code\": \"UNAUTHORIZED\"}",
						message)
		);
	}
}