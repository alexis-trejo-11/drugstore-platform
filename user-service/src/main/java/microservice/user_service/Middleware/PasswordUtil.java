package microservice.user_service.Middleware;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class PasswordUtil {

    // Create a BCryptPasswordEncoder instance
    private static final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    /**
     * Hashes a plain text password.
     *
     * @param plainPassword the plain text password to hash
     * @return the hashed password
     */
    public static String hashPassword(String plainPassword) {
        return passwordEncoder.encode(plainPassword);
    }

    /**
     * Validates a plain text password against a hashed password.
     *
     * @param plainPassword the plain text password
     * @param hashedPassword the hashed password
     * @return true if the passwords match, false otherwise
     */
    public static boolean validatePassword(String plainPassword, String hashedPassword) {
        return passwordEncoder.matches(plainPassword, hashedPassword);
    }
}