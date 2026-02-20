package io.github.alexisTrejo11.drugstore.inventories.inventory.core.inventory.domain.entity.valueobject;


import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public record ExpirationInfo(
        LocalDateTime expirationDate,
        Integer warningDays
) {
    public boolean isExpired() {
        return expirationDate.isBefore(LocalDateTime.now());
    }

    public boolean isExpiringSoon() {
        long daysUntilExpiration = ChronoUnit.DAYS.between(LocalDateTime.now(), expirationDate);
        return daysUntilExpiration <= warningDays && daysUntilExpiration > 0;
    }

    public long getDayUntilExpiration() {
        return ChronoUnit.DAYS.between(LocalDateTime.now(), expirationDate);
    }

}
