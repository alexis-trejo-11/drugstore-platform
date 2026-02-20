package org.github.alexisTrejo11.drugstore.stores.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class StoreStatusChangedEvent {
    private StoreID storeId;
    private StoreStatus previousStatus;
    private StoreStatus newStatus;
    private LocalDateTime occurredOn;
    private String reason;


    public static StoreStatusChangedEvent storeActivate(StoreID id, StoreStatus previousStatus) {
        return new StoreStatusChangedEvent(
                id,
                previousStatus,
                StoreStatus.ACTIVE,
                LocalDateTime.now(),
                ""
        );
    }
}
