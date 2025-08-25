package microservice.user_service.auth.core.application.listeners;

import java.util.UUID;

import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.user_service.auth.core.domain.event.UserRegisteredEvent;
import microservice.user_service.users.core.domain.models.entities.Profile;
import microservice.user_service.users.core.domain.models.enums.UserRole;
import microservice.user_service.users.core.ports.output.ProfileRepository;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserRegisteredEventListener {
    private final ProfileRepository profileRepository;

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
        if (event.getUserType() == UserRole.CUSTOMER.name()) {
            UUID customerId = UUID.randomUUID();

            Profile customerProfile = Profile.CreateCustomerUserProfile(
                    event.getUserId(),
                    customerId.toString(),
                    event.getFirstName(),
                    event.getLastName(),
                    event.getDateOfBirth(),
                    event.getGender());
            profileRepository.save(customerProfile);
        } else if (event.getUserType() == UserRole.EMPLOYEE.name()) {
            UUID employeeId = UUID.randomUUID();

            Profile employeeProfile = Profile.CreateEmployeeUserProfile(
                    event.getUserId(),
                    employeeId.toString(),
                    event.getFirstName(),
                    event.getLastName(),
                    event.getDateOfBirth(),
                    event.getGender());
            profileRepository.save(employeeProfile);
        } else {
            throw new IllegalArgumentException("Unsupported user type: " + event.getUserType());
        }

        log.info("Created customer profile for userId: {}", event.getUserId());

    }

    private void proccessCustomerEmployee(UserRegisteredEvent event) {
        System.out.println("Processing customer or employee profile creation...");
    }

    private void processRegisterActivationEvent(UserRegisteredEvent event) {
        System.out.println("Sending activation email to " + event.getUserId());
    }
}
