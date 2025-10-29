package microservice.inventory_service.order.sales.core.domain.entity;

import microservice.inventory_service.order.sales.core.domain.entity.valueobject.*;
import microservice.inventory_service.order.sales.core.domain.exception.PurchaseOrderStatusValidationException;
import microservice.inventory_service.order.sales.core.domain.exception.PurchaseOrderValidationException;
import microservice.inventory_service.shared.domain.order.BaseOrderDomain;
import microservice.inventory_service.shared.domain.order.OrderStatus;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class SaleOrder extends BaseOrderDomain<SaleOrderId> {
    private DeliveryMethod deliveryMethod;
    private final String customerUserId;
    private String deliveryInfoId;
    private String pickupInfoId;

    private List<SalesOrderItem> items;

    private SaleOrder(SaleOrderId id, LocalDateTime createdAt, LocalDateTime updatedAt,
                      String paymentId, OrderStatus status, String notes,
                      DeliveryMethod deliveryMethod, String customerUserId,
                      String deliveryInfoId, String pickupInfoId, List<SalesOrderItem> items) {
        super(id, createdAt, updatedAt, paymentId, status, notes);
        this.deliveryMethod = deliveryMethod;
        this.customerUserId = customerUserId;
        this.deliveryInfoId = deliveryInfoId;
        this.pickupInfoId = pickupInfoId;
        this.items = new ArrayList<>(items);
    }

    public SaleOrder(SaleOrderId id, String paymentId, OrderStatus status, String notes, DeliveryMethod deliveryMethod, String customerUserId, String deliveryInfoId, String pickupInfoId, List<SalesOrderItem> items) {
        super(id, paymentId, status, notes);
        this.deliveryMethod = deliveryMethod;
        this.customerUserId = customerUserId;
        this.deliveryInfoId = deliveryInfoId;
        this.pickupInfoId = pickupInfoId;
        this.items = items;
    }

    public static SaleOrder reconstitute(SalesOrderFullParams params) {
        return new SaleOrder(
                params.id(),
                params.createdAt(),
                params.updatedAt(),
                params.paymentId(),
                params.status(),
                params.notes(),
                params.deliveryMethod(),
                params.customerUserId(),
                params.deliveryInfoId(),
                params.pickupInfoId(),
                params.items()
        );
    }

    public static SaleOrder createNew(CreateSalesOrderParams params) {
        return new SaleOrder(
                params.id(),
                LocalDateTime.now(), // createdAt
                LocalDateTime.now(), // updatedAt
                null,                // paymentId
                OrderStatus.PENDING_APPROVAL,
                params.notes(),
                params.deliveryMethod(),
                params.customerUserId(),
                params.deliveryInfoId(),
                params.pickupInfoId(),
                params.items()
        );
    }

    public SaleOrder updateOrderDetails(String notes, DeliveryMethod deliveryMethod, String deliveryInfoId, String pickupInfoId, List<SalesOrderItem> items) {
        if (this.status != OrderStatus.PENDING_APPROVAL) {
            throw new PurchaseOrderStatusValidationException(this.status, OrderStatus.PENDING_APPROVAL, "update order details");
        }
        if (items == null || items.isEmpty())
            throw new PurchaseOrderValidationException("Sales order must contain at least one item.");

        this.notes = notes;
        this.deliveryMethod = deliveryMethod;
        this.deliveryInfoId = deliveryInfoId;
        this.pickupInfoId = pickupInfoId;
        this.items = items;

        return this;
    }

    public void confirmPayment(String paymentId) {
        if (this.status != OrderStatus.PENDING_APPROVAL) {
            throw new PurchaseOrderStatusValidationException(this.status, OrderStatus.PENDING_APPROVAL, "confirm payment");
        }

        if (this.paymentId != null) {
            throw new PurchaseOrderValidationException("Payment has already been confirmed for this order.");
        }

        this.paymentId = paymentId;
        this.status = OrderStatus.APPROVED;
    }

    public void fulfillOrder() {
        if (this.getStatus() != OrderStatus.APPROVED) {
            throw new PurchaseOrderStatusValidationException(this.status, OrderStatus.APPROVED, "fulfill order");
        }

        this.status = OrderStatus.FULFILLED;
    }

    public void cancelOrder(String reason) {
        if (this.status == OrderStatus.CANCELLED) {
            throw new PurchaseOrderStatusValidationException(this.status, OrderStatus.CANCELLED, "cancel order");
        }

        this.status = OrderStatus.CANCELLED;
        this.notes = reason;
    }


    public void readyToDelivery() {
        if (this.status != OrderStatus.APPROVED) {
            throw new PurchaseOrderStatusValidationException(this.status, OrderStatus.APPROVED, "mark order as ready for delivery");
        }

        this.status = OrderStatus.READY_FOR_LEAVE;
    }

    public DeliveryMethod getDeliveryMethod() {
        return deliveryMethod;
    }

    public String getCustomerUserId() {
        return customerUserId;
    }

    public String getDeliveryInfoId() {
        return deliveryInfoId;
    }

    public String getPickupInfoId() {
        return pickupInfoId;
    }

    public List<SalesOrderItem> getItems() {
        return items;
    }
}
