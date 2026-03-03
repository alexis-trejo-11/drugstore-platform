package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.domain.valueobjects.Token;

import java.util.List;
import java.util.Optional;

public interface TokenRepository {
	List<Token> getUserTokens(String userId);
	void create(Token token);
	Optional<Token> get(String token);
	void delete(String tokenCode);
	void delete(String tokenCode, String userId);
}
