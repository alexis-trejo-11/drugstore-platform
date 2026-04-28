package io.github.alexisTrejo11.drugstore.products.config;

import java.util.Arrays;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http
				.cors(Customizer.withDefaults())
				// Disable CSRF (Common for REST APIs)
				.csrf(AbstractHttpConfigurer::disable)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.GET, "/api/v2/products/**").permitAll()
						.requestMatchers(HttpMethod.POST, "/api/v2/products/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PUT, "/api/v2/products/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.PATCH, "/api/v2/products/**").hasRole("ADMIN")
						.requestMatchers(HttpMethod.DELETE, "/api/v2/products/**").hasRole("ADMIN")

						.requestMatchers("/actuator/**").hasRole("ADMIN")
						.requestMatchers(
								"/v3/api-docs/**",
								"/swagger-ui/**",
								"/swagger-ui.html",
								"/actuator/health",
								"/actuator/info",
								"/error"
						).permitAll()
						.anyRequest().authenticated()
				)
				.requiresChannel(channel -> channel
						// Enforce HTTPS
						.anyRequest().requiresSecure())
		;

		return http.build();
	}

	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuration = new CorsConfiguration();

		// Define allowed origins (Use "*" for development, but specify domains for
		// production)
		configuration.setAllowedOrigins(
				List.of("http://localhost:4200", "http://localhost:3000", "http://localhost:8000",
						"https://localhost:8444"));

		// Define allowed methods
		configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));

		// Define allowed headers
		configuration.setAllowedHeaders(Arrays.asList("Authorization", "Content-Type", "X-Requested-With"));

		// Allow sending cookies or auth headers
		configuration.setAllowCredentials(true);

		UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		source.registerCorsConfiguration("/**", configuration);
		return source;
	}
}
