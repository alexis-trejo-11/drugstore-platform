package io.github.alexisTrejo11.drugstore.accounts.config;

import javax.crypto.SecretKey;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.jsonwebtoken.security.Keys;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
  private String secret;
  private long accessTokenExpirationMinutes = 60;
  private long refreshTokenExpirationMinutes = 43800; // 30 days

  public JwtConfig() {
  }

  public JwtConfig(String secret, long accessTokenExpirationMinutes, long refreshTokenExpirationMinutes) {
    this.secret = secret;
    this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
    this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;
  }

  @Bean
  public SecretKey secretKey() {
    return Keys.hmacShaKeyFor(secret.getBytes());
  }

  public String getSecret() {
    return secret;
  }

  public void setSecret(String secret) {
    this.secret = secret;
  }

  public long getAccessTokenExpirationMinutes() {
    return accessTokenExpirationMinutes;
  }

  public void setAccessTokenExpirationMinutes(long accessTokenExpirationMinutes) {
    this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
  }

  public void setRefreshTokenExpirationMinutes(long refreshTokenExpirationMinutes) {
    this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;
  }

  public long getRefreshTokenExpirationMinutes() {
    return refreshTokenExpirationMinutes;
  }

}
