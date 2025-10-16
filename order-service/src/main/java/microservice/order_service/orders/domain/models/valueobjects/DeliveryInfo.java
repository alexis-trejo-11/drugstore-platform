package microservice.order_service.orders.domain.models.valueobjects;

import lombok.Getter;
import lombok.Setter;
import microservice.order_service.external.address.domain.model.AddressID;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class DeliveryInfo {
    private String id;
    private String trackingNumber;
    private Integer deliveryAttempt;
    private Money shippingCost;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;
    private String receiverName;
    private Money deliveryFee;
    private AddressID addressID;


    public DeliveryInfo(String id, String trackingNumber, Integer deliveryAttempt, LocalDateTime estimatedDeliveryDate, LocalDateTime actualDeliveryDate, Money shippingCost) {
        if (deliveryAttempt != null && deliveryAttempt < 0) {
            throw new IllegalArgumentException("Delivery attempt cannot be negative");
        }
        this.trackingNumber = trackingNumber;
        this.deliveryAttempt = deliveryAttempt;
        this.estimatedDeliveryDate = estimatedDeliveryDate;
        this.actualDeliveryDate = actualDeliveryDate;
        this.shippingCost = shippingCost;
    }

    public void incrementDeliveryAttempt() {
        if (this.deliveryAttempt == null) {
            this.deliveryAttempt = 1;
        } else {
            this.deliveryAttempt++;
        }
    }

    public static DeliveryInfo create(LocalDateTime estimatedDeliveryDate, Money shippingCost) {
        return new DeliveryInfo(UUID.randomUUID().toString(), null,0, estimatedDeliveryDate, null, shippingCost);
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

