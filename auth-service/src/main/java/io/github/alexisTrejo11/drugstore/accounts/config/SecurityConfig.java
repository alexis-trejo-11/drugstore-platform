package io.github.alexisTrejo11.drugstore.accounts.config;

import io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.security.OAuth2AuthenticationSuccessHandler;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.CustomOAuth2UserService;
import libs_kernel.security.jwt.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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


  @Bean
  public SecurityFilterChain filterChain(
			HttpSecurity http,
			CustomOAuth2UserService oauthUserService,
			OAuth2AuthenticationSuccessHandler successHandler) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session
            .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers(
                "/v3/api-docs/**",
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/actuator/health",
                "/actuator/info",
                "/api/v2/auth/login/**",
                "/api/v2/auth/password/**",
                "/api/v2/auth/register/**",
		            "/api/v2/auth/**",
		            "/login/**",
		            "/oauth2/**",
                "/error")
            .permitAll()
            .anyRequest().permitAll())

		    .oauth2Login(oauth2 -> oauth2
				    .userInfoEndpoint(userInfo -> userInfo.userService(oauthUserService))
				    .successHandler(successHandler)
		    )
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
    return http.build();
  }
}