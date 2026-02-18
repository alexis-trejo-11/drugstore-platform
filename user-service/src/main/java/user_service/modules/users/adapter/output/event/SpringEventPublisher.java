package user_service.modules.users.adapter.output.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import user_service.modules.users.core.domain.events.DomainEvent;
import user_service.modules.users.core.ports.output.EventPublisher;

@Component
public class SpringEventPublisher implements EventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(DomainEvent event) {
        applicationEventPublisher.publishEvent(event);
    }
}
