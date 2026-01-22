package microservice.product_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        // 1. Enable CORS using the source defined below
        .cors(Customizer.withDefaults())

        // 2. Disable CSRF (Common for REST APIs)
        .csrf(AbstractHttpConfigurer::disable)

        // 3. Set permissions
        .authorizeHttpRequests(auth -> auth
            .anyRequest().permitAll())

    // 4. Placeholder for future Authentication
    // .httpBasic(Customizer.withDefaults())
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
            "http://localhost:80840"));

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
