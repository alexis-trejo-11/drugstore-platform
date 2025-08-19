package microservice.users.utils.password;

import org.springframework.security.crypto.bcrypt.BCrypt;

public class PasswordEncoder {

    public String hashPassword(String password) {
            return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public boolean verifyPassword(String plainPassword, String hashedPassword) {
        return BCrypt.checkpw(plainPassword, hashedPassword);
    }
}

