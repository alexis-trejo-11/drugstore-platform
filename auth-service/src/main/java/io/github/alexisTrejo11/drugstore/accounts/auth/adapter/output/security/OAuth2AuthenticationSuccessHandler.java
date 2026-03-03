package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security;

import java.io.IOException;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.TokenType;
import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.tokens.factory.TokenFactory;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import libs_kernel.security.dto.UserClaims;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Handles successful OAuth2 authentication by generating tokens and redirecting
 * to frontend.
 * Implements security best practices for OAuth2 flows.
 */
@Component
@Slf4j
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

  private final TokenFactory tokenFactory;

  @Value("${oauth2.redirect-url:http://localhost:3000/oauth2/redirect}")
  private String frontendRedirectUrl;

  @Override
  public void onAuthenticationSuccess(HttpServletRequest request,
      HttpServletResponse response,
      Authentication authentication) throws IOException {

    if (response.isCommitted()) {
      log.warn("Response already committed. Cannot redirect.");
      return;
    }

    try {
      OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
      String email = extractEmail(oAuth2User);

      if (email == null || email.isBlank()) {
        log.error("Failed to extract email from OAuth2 user");
        handleAuthenticationFailure(request, response, "Email not provided by OAuth2 provider");
        return;
      }

      log.info("OAuth2 authentication successful for email: {}", email);

      // Build UserClaims for token generation
      UserClaims userClaims = UserClaims.builder()
          .userId(email) // Use email as userId for OAuth2 users
          .email(email)
          .name(oAuth2User.getAttribute("name"))
          .role(oAuth2User.getAttribute("roles"))
          .build();

      // Generate tokens using the centralized factory
      String refreshToken = tokenFactory.createToken(TokenType.REFRESH, userClaims).code();
      String accessToken = tokenFactory.createToken(TokenType.ACCESS, userClaims).code();

      // Construct frontend redirect URL with tokens
      String targetUrl = UriComponentsBuilder.fromUriString(frontendRedirectUrl)
          .queryParam("token", accessToken)
          .queryParam("refreshToken", refreshToken)
          .build().toUriString();

      log.debug("Redirecting OAuth2 user to frontend");

      // Redirect to frontend
      getRedirectStrategy().sendRedirect(request, response, targetUrl);

    } catch (Exception e) {
      log.error("Error processing OAuth2 authentication success: {}", e.getMessage(), e);
      handleAuthenticationFailure(request, response, "Internal authentication error");
    }
  }

  /**
   * Extracts email from OAuth2 user attributes.
   * Supports multiple OAuth2 providers (Google, GitHub, etc.)
   */
  private String extractEmail(OAuth2User oAuth2User) {
    if (oAuth2User == null) {
      return null;
    }

    Map<String, Object> attributes = oAuth2User.getAttributes();

    // Google uses "email", GitHub may vary if private
    if (attributes.containsKey("email")) {
      return (String) attributes.get("email");
    }

    // Fallback to username/name for providers without email
    String name = oAuth2User.getName();
    log.warn("Email not found in OAuth2 attributes, using name as fallback: {}", name);
    return name;
  }

  /**
   * Handles authentication failure by redirecting with error message.
   */
  private void handleAuthenticationFailure(HttpServletRequest request,
      HttpServletResponse response, String errorMessage) throws IOException {
    String errorUrl = UriComponentsBuilder.fromUriString(frontendRedirectUrl)
        .queryParam("error", errorMessage)
        .build().toUriString();

    getRedirectStrategy().sendRedirect(request, response, errorUrl);
  }
}
