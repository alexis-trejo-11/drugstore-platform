package org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
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
