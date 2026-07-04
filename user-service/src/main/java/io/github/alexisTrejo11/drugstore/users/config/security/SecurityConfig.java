package io.github.alexisTrejo11.drugstore.users.config.security;


import io.github.alexisTrejo11.drugstore.users.config.security.jwt.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {
	private final JwtAuthenticationFilter jwtAuthenticationFilter;


	@Autowired
	public SecurityConfig(
			JwtAuthenticationFilter jwtAuthenticationFilter) {
		this.jwtAuthenticationFilter = jwtAuthenticationFilter;
	}

	/**
	 * OpenAPI / Swagger UI serves HTML plus static assets under
	 * {@code /webjars/**}.
	 * Those requests must not go through the security filter chain: with MVC on the
	 * classpath, {@code requestMatchers("/swagger-ui/**")} inside
	 * {@code HttpSecurity}
	 * can fail to match resource handler paths, which then hit
	 * {@code authenticated()}
	 * and trigger an auth entry point (often seen as a “login” redirect in the
	 * browser).
	 */
	@Bean
	public WebSecurityCustomizer swaggerOpenApiWebSecurityCustomizer() {
		return (web) -> web.ignoring().requestMatchers(
				"/swagger-ui/**",
				"/swagger-ui.html",
				"/swagger-ui/index.html",
				"/v3/api-docs",
				"/v3/api-docs/**",
				"/v3/api-docs.yaml",
				"/webjars/**",
				"/swagger-resources/**");
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
						.requestMatchers("/api/v2/health/**").permitAll()
						.requestMatchers("/actuator/**").permitAll()
						.requestMatchers("/error", "/error/**").permitAll()
						.requestMatchers("/favicon.ico").permitAll()
						.requestMatchers("/api/v2/users/profile/**").authenticated()
						.requestMatchers("/api/v2/users/manager/**").hasAnyRole("ADMIN", "MANAGER")
						.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html").permitAll()
						.requestMatchers("/api/**").authenticated()
						.anyRequest().denyAll())
				.formLogin(AbstractHttpConfigurer::disable)
				.httpBasic(AbstractHttpConfigurer::disable)
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}
}