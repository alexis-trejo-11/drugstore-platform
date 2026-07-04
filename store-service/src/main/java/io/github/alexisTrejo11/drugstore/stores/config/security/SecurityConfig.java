package io.github.alexisTrejo11.drugstore.stores.config.security;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  @Autowired
  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .cors(Customizer.withDefaults())
        .csrf(AbstractHttpConfigurer::disable)
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        // Stateless API: never save requests for a post-login redirect.
        .requestCache(AbstractHttpConfigurer::disable)
        .logout(AbstractHttpConfigurer::disable)
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/swagger-ui/index.html",
                "/v3/api-docs",
                "/v3/api-docs/**",
                "/v3/api-docs.yaml",
                "/webjars/**",
                "/swagger-resources/**")
            .permitAll()
            .requestMatchers(
                "/actuator/health",
                "/actuator/health/**",
                "/actuator/info",
                "/actuator/prometheus")
            .permitAll()
            .requestMatchers(HttpMethod.GET, "/api/v2/stores/**").permitAll()
            .requestMatchers(HttpMethod.POST, "/api/v2/stores/**").hasAnyRole("ADMIN", "MANAGER")
            .requestMatchers(HttpMethod.PUT, "/api/v2/stores/**").hasAnyRole("ADMIN",  "MANAGER")
            .requestMatchers(HttpMethod.PATCH, "/api/v2/stores/**").hasAnyRole("ADMIN",  "MANAGER")
            .requestMatchers(HttpMethod.DELETE, "/api/v2/stores/**").hasAnyRole("ADMIN",  "MANAGER")
            .anyRequest().denyAll())
        .formLogin(AbstractHttpConfigurer::disable)
        .httpBasic(AbstractHttpConfigurer::disable)
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}
