package io.github.alexisTrejo11.drugstore.users.user.adapter.output.event;

import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

import io.github.alexisTrejo11.drugstore.users.user.core.domain.events.DomainEvent;
import io.github.alexisTrejo11.drugstore.users.user.core.ports.output.EventPublisher;

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
