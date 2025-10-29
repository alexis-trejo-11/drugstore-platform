package microservice.inventory_service.shared.domain.order;

import java.time.LocalDateTime;
import java.util.Objects;

public abstract class BaseDomainEntity<T> {
    protected final T id;
    protected final LocalDateTime createdAt;
    protected LocalDateTime updatedAt;
    protected LocalDateTime deletedAt;

    protected BaseDomainEntity(T id, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deletedAt = null;
    }

    protected BaseDomainEntity(T id) {
        this(id, LocalDateTime.now(), LocalDateTime.now());
    }

    public T getId() { return id; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public LocalDateTime getDeletedAt() { return deletedAt; }
    
    public void markAsUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
    
    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }
    
    public boolean isDeleted() {
        return deletedAt != null;
    }

    // Equals y HashCode basados en ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        BaseDomainEntity<?> that = (BaseDomainEntity<?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}