package microservice.order_service.orders.domain.models.valueobjects;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
public class DeliveryInfo {
    private String trackingNumber;
    private Integer deliveryAttempt;
    private Money shippingCost;
    private LocalDateTime estimatedDeliveryDate;
    private LocalDateTime actualDeliveryDate;


    public DeliveryInfo(String trackingNumber, Integer deliveryAttempt, LocalDateTime estimatedDeliveryDate, LocalDateTime actualDeliveryDate, Money shippingCost) {
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

    public static DeliveryInfo create(String trackingNumber, LocalDateTime estimatedDeliveryDate, Money shippingCost) {
        return new DeliveryInfo(trackingNumber, 0, estimatedDeliveryDate, null, shippingCost);
    }

    public void removeTrackingNumber() {
        this.trackingNumber = null;
    }
}

