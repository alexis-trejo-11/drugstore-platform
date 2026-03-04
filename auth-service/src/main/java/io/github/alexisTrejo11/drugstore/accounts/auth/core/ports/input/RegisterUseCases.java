package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.SignupCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.result.SignUpResult;

public interface RegisterUseCases {
  SignUpResult register(SignupCommand command);

  void activateAccount(String activationCode);
}
