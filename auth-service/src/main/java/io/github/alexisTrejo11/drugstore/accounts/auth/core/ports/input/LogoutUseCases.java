package io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.input;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutAllCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.LogoutCommand;

public interface LogoutUseCases {
  void logout(LogoutCommand command);

  void logoutAll(LogoutAllCommand userId);
}
