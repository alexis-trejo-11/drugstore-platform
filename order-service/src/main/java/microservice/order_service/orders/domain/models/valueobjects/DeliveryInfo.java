package microservice.order_service.orders.domain.models.valueobjects;

import lombok.*;
import microservice.order_service.external.address.domain.model.AddressID;
import microservice.order_service.external.address.domain.model.DeliveryAddress;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DeliveryInfo {
    private String id;
    private String trackingNumber;
    private Integer deliveryAttempt;
    private Money shippingCost;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String receiverName;
    private Money deliveryFee;
    private DeliveryAddress address;

    public void incrementDeliveryAttempt() {
        if (this.deliveryAttempt == null) {
            this.deliveryAttempt = 1;
        } else {
            this.deliveryAttempt++;
        }
    }

    public static DeliveryInfo create(LocalDateTime estimatedDeliveryDate, Money shippingCost, Money deliveryFee, DeliveryAddress deliveryAddress) {
        return DeliveryInfo.builder()
                .id(UUID.randomUUID().toString())
                .estimatedDeliveryDate(estimatedDeliveryDate)
                .shippingCost(shippingCost)
                .deliveryFee(deliveryFee)
                .address(deliveryAddress)
                .deliveryAttempt(0)
                .build();
    }

    public void ship(String trackingNumber) {
        if (this.trackingNumber != null) {
            throw new IllegalStateException("Order is already shipped");
        }

        this.trackingNumber = trackingNumber;
    }

    public void removeTrackingNumber() {
        this.trackingNumber = null;
    }
}

