package microservice.inventory_service.shared.domain;

import org.springframework.context.ApplicationEventPublisher;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class AggregateRoot<ID> {
    private final List<Object> domainEvents = new ArrayList<>();
    
    public List<Object> getDomainEvents() {
        return Collections.unmodifiableList(domainEvents);
    }
    
    public void clearDomainEvents() {
        domainEvents.clear();
    }
    
    protected void registerEvent(Object event) {
        domainEvents.add(event);
    }
    
    public void publishEvents(ApplicationEventPublisher publisher) {
        domainEvents.forEach(publisher::publishEvent);
        clearDomainEvents();
    }
}