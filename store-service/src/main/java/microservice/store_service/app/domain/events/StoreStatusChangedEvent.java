package microservice.store_service.app.domain.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.domain.model.valueobjects.StoreID;

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
