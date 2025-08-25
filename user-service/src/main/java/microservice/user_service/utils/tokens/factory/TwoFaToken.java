package microservice.user_service.utils.tokens.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import microservice.user_service.utils.tokens.interfaces.NumericToken;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class TwoFaToken implements NumericToken {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final Map<String, TokenData> TOKEN_STORE = new ConcurrentHashMap<>();

    @Value("${token.two-fa.length:6}")
    private int tokenLength;

    @Value("${token.two-fa.expiration:300000}") // 5 minutos por defecto
    private long expirationTime;

    private String email;

    @Override
    public String generate() {
        if (email == null || email.isBlank()) {
            throw new IllegalStateException("Email must be set before generating 2FA token");
        }

        // Generar token numérico
        int min = (int) Math.pow(10, tokenLength - 1);
        int max = (int) Math.pow(10, tokenLength) - 1;
        String token = String.valueOf(min + RANDOM.nextInt(max - min + 1));

        // Almacenar token con timestamp
        TOKEN_STORE.put(email, new TokenData(token, LocalDateTime.now()));

        return token;
    }

    @Override
    public LocalDateTime expiresAt() {
        TokenData tokenData = TOKEN_STORE.get(email);
        if (tokenData == null) {
            return null;
        }
        return tokenData.getCreatedAt().plus(expirationTime, ChronoUnit.MILLIS);
    }

    @Override
    public boolean validate(String token) {
        if (email == null || email.isBlank() || token == null || token.isBlank()) {
            return false;
        }

        TokenData tokenData = TOKEN_STORE.get(email);
        if (tokenData == null) {
            return false;
        }

        // Verificar si el token ha expirado
        long minutesElapsed = ChronoUnit.MILLIS.between(tokenData.getCreatedAt(), LocalDateTime.now());
        if (minutesElapsed > expirationTime) {
            TOKEN_STORE.remove(email); // Limpiar token expirado
            return false;
        }

        // Verificar coincidencia del token
        boolean isValid = tokenData.getToken().equals(token);

        // Limpiar token después de validación (uso único)
        if (isValid) {
            TOKEN_STORE.remove(email);
        }

        return isValid;
    }

    @Override
    public String getType() {
        return "TWO_FA";
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

    public int getTokenLength() {
        return tokenLength;
    }

    public long getExpirationTime() {
        return expirationTime;
    }

    public void cleanExpiredTokens() {
        LocalDateTime now = LocalDateTime.now();
        TOKEN_STORE.entrySet().removeIf(entry -> {
            long minutesElapsed = ChronoUnit.MILLIS.between(entry.getValue().getCreatedAt(), now);
            return minutesElapsed > expirationTime;
        });
    }

    private static class TokenData {
        private final String token;
        private final LocalDateTime createdAt;

        public TokenData(String token, LocalDateTime createdAt) {
            this.token = token;
            this.createdAt = createdAt;
        }

        public String getToken() {
            return token;
        }

        public LocalDateTime getCreatedAt() {
            return createdAt;
        }
    }
}