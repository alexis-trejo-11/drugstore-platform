package microservice.inventory_service.shared.domain.order;

import java.time.LocalDateTime;

public abstract class BaseOrderDomain<T> extends BaseDomainEntity<T> {
    protected String paymentId;
    protected OrderStatus status;
    protected String notes;

    protected BaseOrderDomain(T id, LocalDateTime createdAt, LocalDateTime updatedAt, String paymentId, OrderStatus status, String notes) {
        super(id, createdAt, updatedAt);
        this.paymentId = paymentId;
        this.status = status;
        this.notes = notes;
    }

    protected BaseOrderDomain(T id, String paymentId, OrderStatus status, String notes) {
        super(id);
        this.paymentId = paymentId;
        this.status = status;
        this.notes = notes;
    }

    protected BaseOrderDomain(T id, OrderStatus status, String notes) {
        super(id);
        this.status = status;
        this.notes = notes;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public String getNotes() {
        return notes;
    }
}