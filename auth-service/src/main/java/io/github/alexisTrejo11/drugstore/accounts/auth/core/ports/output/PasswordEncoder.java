package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output;

public interface PasswordEncoder {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}