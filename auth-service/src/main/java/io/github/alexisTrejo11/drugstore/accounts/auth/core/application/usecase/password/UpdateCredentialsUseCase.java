package io.github.alexisTrejo11.drugstore.accounts.auth.core.application.usecase.password;

import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.accounts.auth.core.application.command.password.UpdateCredentialsCommand;
import io.github.alexisTrejo11.drugstore.accounts.auth.core.ports.output.UserServiceClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class UpdateCredentialsUseCase {

  private final UserServiceClient userServiceClient;

  public void execute(UpdateCredentialsCommand command) {
    String uid = command.userId().value();
    String email = command.newEmail() != null ? command.newEmail().trim() : "";
    String phone = command.newPhone() != null ? command.newPhone().trim() : "";

    userServiceClient.updateUserCredentials(uid, email, phone);
    log.info("Credentials update requested for user {}", uid);
  }
}
