package io.github.alexisTrejo11.drugstore.accounts.config.security;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.OAuth2AuthenticationSuccessHandler;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.CustomOAuth2UserService;
import io.github.alexisTrejo11.drugstore.accounts.config.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.http.HttpMethod;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  private static final String[] PUBLIC_ENDPOINTS = {
    "/v3/api-docs/**",
    "/swagger-ui/**",
    "/swagger-ui.html",
    "/actuator/health",
    "/actuator/info",
    "/api/v2/auth/register/**",
    "/api/v2/auth/login",
    "/api/v2/auth/login/2fa",
    "/api/v2/auth/session/refresh",
    "/api/v2/auth/password/forgot",
    "/api/v2/auth/password/validate-token",
    "/api/v2/auth/password/reset",
    "/api/v2/auth/activate",
    "/api/v2/auth/logout",
    "/login/oauth2/**",
    "/oauth2/**",
    "/error"
  };

  @Bean
  public SecurityFilterChain filterChain(
      HttpSecurity http,
      ObjectProvider<ClientRegistrationRepository> clientRegistrations,
      ObjectProvider<CustomOAuth2UserService> oauthUserService,
      ObjectProvider<OAuth2AuthenticationSuccessHandler> successHandler)
      throws Exception {
    http.csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers(HttpMethod.OPTIONS, "/**")
                    .permitAll()
                    .requestMatchers(PUBLIC_ENDPOINTS)
                    .permitAll()
                    .requestMatchers("/api/v2/auth/**")
                    .authenticated()
                    .anyRequest()
                    .denyAll());

    // OAuth2 client auto-config only creates ClientRegistrationRepository when
    // spring.security.oauth2.client.registration.* is fully configured (e.g. Docker without Google keys).
    if (clientRegistrations.getIfAvailable() != null) {
      http.oauth2Login(
          oauth2 ->
              oauth2
                  .userInfoEndpoint(
                      userInfo -> userInfo.userService(oauthUserService.getObject()))
                  .successHandler(successHandler.getObject()));
    }

    http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}
