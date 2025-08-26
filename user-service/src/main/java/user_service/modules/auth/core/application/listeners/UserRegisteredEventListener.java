package user_service.modules.auth.core.application.listeners;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import user_service.modules.auth.core.domain.event.UserRegisteredEvent;
import user_service.modules.profile.core.application.usecase.ProfileUseCases;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventListener {
    private final ProfileUseCases profileUseCases;

    @Async("taskExecutor")
    @EventListener
    public void handleUserRegisteredEvent(UserRegisteredEvent event) {
        if (event == null || event.getUserId() == null) {
            return;
        }

        log.info("Handling UserRegisteredEvent for userId: {}", event.getUserId());

        processProfileEvent(event);
        proccessCustomerEmployee(event);
        processRegisterActivationEvent(event);
    }

    private void processProfileEvent(UserRegisteredEvent event) {
        profileUseCases.createProfile(null, null);
        log.info("Created customer profile for userId: {}", event.getUserId());
    }

    private void proccessCustomerEmployee(UserRegisteredEvent event) {
        System.out.println("Processing customer or employee profile creation...");
    }

    private void processRegisterActivationEvent(UserRegisteredEvent event) {
        System.out.println("Sending activation email to " + event.getUserId());
    }
}
