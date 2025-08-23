package microservice.user_service.tokens.factory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import microservice.user_service.tokens.interfaces.NumericToken;

@Component
@RequiredArgsConstructor
public class ActivationToken implements NumericToken {
    @Value("${token.activation.length}")
    private final int numericTokenLength;
    private String email;

    @Override
    public String generate() {
        int min = (int) Math.pow(10, numericTokenLength - 1);
        int max = (int) Math.pow(10, numericTokenLength) - 1;
        return String.valueOf(min + (int) (Math.random() * ((max - min) + 1)));

    }

    @Override
    public boolean validate(String token) {
        if (token == null || token.length() != numericTokenLength) {
            return false;
        }
        for (char c : token.toCharArray()) {
            if (!Character.isDigit(c)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String getType() {
        return "Activation";
    }

    @Override
    public String getEmail() {
        return this.email;
    }

    @Override
    public void setEmail(String email) {
        this.email = email;
    }

}
