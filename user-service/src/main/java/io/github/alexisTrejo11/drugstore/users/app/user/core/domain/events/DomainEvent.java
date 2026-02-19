package io.github.alexisTrejo11.drugstore.users.app.user.core.domain.events;

import lombok.Getter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
public abstract class DomainEvent {
    protected final UUID eventId;
    protected final LocalDateTime occurredOn;

    protected DomainEvent() {
        this.eventId = UUID.randomUUID();
        this.occurredOn = LocalDateTime.now();
    }
}
