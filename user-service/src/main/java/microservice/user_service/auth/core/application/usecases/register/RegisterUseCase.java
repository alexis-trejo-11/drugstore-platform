package microservice.user_service.auth.core.application.usecases.register;

import lombok.RequiredArgsConstructor;

import microservice.user_service.auth.core.application.dto.RegisterDTO;
import microservice.user_service.auth.core.domain.event.UserRegisteredEvent;
import microservice.user_service.auth.core.domain.service.UserSecurityService;
import microservice.user_service.users.core.domain.models.entities.User;
import microservice.user_service.users.core.ports.output.UserRepository;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.scheduling.annotation.Async;

import microservice.user_service.utils.response.Result;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RegisterUseCase {
    private final UserRepository userRepository;
    private final UserSecurityService userSecurityService;
    private final ApplicationEventPublisher eventPublisher;

    public Result<UUID> execute(RegisterDTO dto) {
        userSecurityService.validateNewUserData(dto.email(), dto.phoneNumber(), dto.password());

        User newUser = userSecurityService.processNewUser(dto.toUser());
        User savedUser = userRepository.save(newUser);

        publishUserRegisteredEventAsync(savedUser, dto);

        return Result.success(savedUser.getId());
    }

    @Async("taskExecutor")
    public void publishUserRegisteredEventAsync(User user, RegisterDTO dto) {
        var event = UserRegisteredEvent.builder()
                .source(this)
                .userId(user.getId())
                .userType(user.getRole().name())
                .firstName(dto.profile().getFirstName())
                .lastName(dto.profile().getLastName())
                .dateOfBirth(dto.profile().getBirthDate())
                .gender(dto.profile().getGender())
                .build();

        eventPublisher.publishEvent(event);
    }
}
