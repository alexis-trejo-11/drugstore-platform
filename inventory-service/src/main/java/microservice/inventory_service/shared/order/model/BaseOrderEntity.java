package microservice.inventory_service.shared.order.model;

import jakarta.persistence.Column;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import microservice.inventory_service.shared.domain.order.OrderStatus;

@MappedSuperclass
@Getter
@Setter
public abstract class BaseOrderEntity extends BaseEntity {
    @Column(name = "payment_id", length = 36)
    protected String paymentId;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    protected OrderStatus status;

    @Column(name = "notes", length = 500)
    protected String notes;

}
