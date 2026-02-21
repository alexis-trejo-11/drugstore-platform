package io.github.alexisTrejo11.drugstore.users.user.adapter.output.messaging.kafka.handler;

import io.github.alexisTrejo11.drugstore.users.user.core.application.command.CreateUserCommand;
import io.github.alexisTrejo11.drugstore.users.user.core.application.result.CommandResult;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.UserCreatedEvent;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.enums.UserRole;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.Email;
import io.github.alexisTrejo11.drugstore.users.user.core.domain.models.valueobjects.FullName;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.input.CommandBus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserCreatedEventHandler {
	private final CommandBus commandBus;
	private final org.slf4j.Logger log = org.slf4j.LoggerFactory.getLogger(UserCreatedEventHandler.class);

	@Autowired
	public UserCreatedEventHandler(CommandBus commandBus) {
		this.commandBus = commandBus;
	}

	public void handle(UserCreatedEvent event) {
		FullName fullName = null;
		if (event.getFirstName() != null || event.getLastName() != null) {
			fullName = new FullName(
					event.getFirstName() != null ? event.getFirstName() : "",
					event.getLastName() != null ? event.getLastName() : ""
			);
		}

		CreateUserCommand command = CreateUserCommand.builder()
				.email(event.getEmail() != null ? new Email(event.getEmail()) : null)
				.password(event.getHashedPassword())
				.isPasswordHashed(true)
				.fullName(fullName)
				.role(UserRole.fromString(event.getRole()))
				.build();

		try {
			CommandResult result = commandBus.dispatch(command);
			if (result.success()) {
				log.info("Successfully created user for email: {}", event.getEmail());
			} else {
				log.error("Failed to create user for email: {}. Message: {}", event.getEmail(), result.message());
			}
		} catch (Exception e) {
			log.error("Error handling UserCreatedEvent for user with email: {}", event.getEmail(), e);
			throw new RuntimeException(e);
		}
	}

}
