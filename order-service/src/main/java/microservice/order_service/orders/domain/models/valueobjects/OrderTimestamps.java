package microservice.order_service.orders.domain.models.valueobjects;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class OrderTimestamps {
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private LocalDateTime deletedAt;

    public static OrderTimestamps create() {
        LocalDateTime now = LocalDateTime.now();
        return new OrderTimestamps(now, now, null);
    }

    public void orderUpdated() {
        this.updatedAt = LocalDateTime.now();
    }
}
