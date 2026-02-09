package libs_kernel.security.jwt;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties{
	private String secret;
	private long accessTokenExpirationMinutes;
	private long refreshTokenExpirationMinutes;
	private String issuer;

	public JwtProperties() {
	}

	public JwtProperties(String secret, long accessTokenExpirationMinutes, long refreshTokenExpirationMinutes, String issuer) {
		this.secret = secret;
		this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
		this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;
		this.issuer = issuer;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}

	// Compatibility: JwtTokenManager expects getters that return a value in milliseconds
	// The configuration YAML uses minutes, so convert minutes -> milliseconds here.
	public long getAccessTokenExpirationSeconds() {
		return accessTokenExpirationMinutes * 60 * 1000;
	}

	public void setAccessTokenExpirationSeconds(long accessTokenExpirationSeconds) {
		// Allow binding if millis were provided; convert to minutes for internal storage
		this.accessTokenExpirationMinutes = accessTokenExpirationSeconds / (60 * 1000);
	}

	public long getRefreshTokenExpirationSeconds() {
		return refreshTokenExpirationMinutes * 60 * 1000;
	}

	public void setRefreshTokenExpirationSeconds(long refreshTokenExpirationSeconds) {
		this.refreshTokenExpirationMinutes = refreshTokenExpirationSeconds / (60 * 1000);
	}

	// Expose minutes getters/setters to support property names like access-token-expiration-minutes
	public long getAccessTokenExpirationMinutes() {
		return accessTokenExpirationMinutes;
	}

	public void setAccessTokenExpirationMinutes(long accessTokenExpirationMinutes) {
		this.accessTokenExpirationMinutes = accessTokenExpirationMinutes;
	}

	public long getRefreshTokenExpirationMinutes() {
		return refreshTokenExpirationMinutes;
	}

	public void setRefreshTokenExpirationMinutes(long refreshTokenExpirationMinutes) {
		this.refreshTokenExpirationMinutes = refreshTokenExpirationMinutes;
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}
}
