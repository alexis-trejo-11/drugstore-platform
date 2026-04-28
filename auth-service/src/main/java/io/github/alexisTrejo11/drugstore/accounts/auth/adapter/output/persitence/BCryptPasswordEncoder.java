package io.github.alexisTrejo11.drugstore.accounts.auth.adapter.output.persitence;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class BCryptPasswordEncoder implements PasswordEncoder {

    private final org.springframework.security.crypto.password.PasswordEncoder encoder;

    public BCryptPasswordEncoder() {
        this.encoder = new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }

    @Override
    public String encode(String rawPassword) {
        return encoder.encode(rawPassword);
    }

    @Override
    public boolean matches(String rawPassword, String encodedPassword) {
        return encoder.matches(rawPassword, encodedPassword);
    }
}