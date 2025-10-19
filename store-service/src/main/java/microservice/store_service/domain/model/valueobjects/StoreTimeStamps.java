package microservice.store_service.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class StoreTimeStamps{
    LocalDateTime createdAt;
    LocalDateTime updatedAt;
    LocalDateTime deletedAt;

    public void markAsUpdated() {
        updatedAt = LocalDateTime.now();
    }

    public static StoreTimeStamps now() {
        LocalDateTime now = LocalDateTime.now();
        return new StoreTimeStamps(now, now, null);
    }

    public void markAsDeleted() {
        this.deletedAt = LocalDateTime.now();
    }

    public void restore() {
        this.deletedAt = null;
    }
}
