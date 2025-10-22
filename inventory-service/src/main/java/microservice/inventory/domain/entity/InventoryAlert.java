package microservice.inventory.domain.entity;

import lombok.Getter;
import microservice.inventory.domain.entity.enums.AlertSeverity;
import microservice.inventory.domain.entity.enums.AlertStatus;
import microservice.inventory.domain.entity.enums.AlertType;
import microservice.inventory.domain.entity.valueobject.id.AlertId;
import microservice.inventory.domain.entity.valueobject.id.InventoryId;
import microservice.inventory.domain.entity.valueobject.id.UserId;
import org.apache.catalina.User;

import java.time.LocalDateTime;

@Getter
public class InventoryAlert {
    private AlertId id;
    private InventoryId inventoryId;
    private AlertType alertType;
    private AlertSeverity severity;
    private String message;
    private AlertStatus status;
    private LocalDateTime triggeredAt;
    private LocalDateTime resolvedAt;
    private UserId resolvedBy;
    private String resolutionNotes;
    private LocalDateTime createdAt;

    private InventoryAlert(AlertId id, InventoryId inventoryId, AlertType alertType, AlertSeverity severity,
                           String message, AlertStatus status, LocalDateTime triggeredAt,
                           LocalDateTime resolvedAt, UserId resolvedBy, String resolutionNotes,
                           LocalDateTime createdAt) {
        this.id = id;
        this.inventoryId = inventoryId;
        this.alertType = alertType;
        this.severity = severity;
        this.message = message;
        this.status = status;
        this.triggeredAt = triggeredAt;
        this.resolvedAt = resolvedAt;
        this.resolvedBy = resolvedBy;
        this.resolutionNotes = resolutionNotes;
        this.createdAt = createdAt;
    }

    public void acknowledge() {
        if (this.status != AlertStatus.ACTIVE) {
            throw new IllegalStateException("Can only acknowledge active alerts");
        }
        this.status = AlertStatus.ACKNOWLEDGED;
    }

    public void resolve(UserId resolvedBy, String resolutionNotes) {
        if (this.status == AlertStatus.RESOLVED || this.status == AlertStatus.DISMISSED) {
            throw new IllegalStateException("Alert already resolved or dismissed");
        }
        this.status = AlertStatus.RESOLVED;
        this.resolvedAt = LocalDateTime.now();
        this.resolvedBy = resolvedBy;
        this.resolutionNotes = resolutionNotes;
    }

    public void dismiss() {
        if (this.status == AlertStatus.RESOLVED || this.status == AlertStatus.DISMISSED) {
            throw new IllegalStateException("Alert already resolved or dismissed");
        }
        this.status = AlertStatus.DISMISSED;
    }

    public boolean isActive() {
        return this.status == AlertStatus.ACTIVE;
    }

    public static InventoryAlertReconstructor reconstructor() {
        return new InventoryAlertReconstructor();
    }

    public static class InventoryAlertReconstructor {
        private AlertId id;
        private InventoryId inventoryId;
        private AlertType alertType;
        private AlertSeverity severity;
        private String message;
        private AlertStatus status;
        private LocalDateTime triggeredAt;
        private LocalDateTime resolvedAt;
        private UserId resolvedBy;
        private String resolutionNotes;
        private LocalDateTime createdAt;

        public InventoryAlertReconstructor id(AlertId id) {
            this.id = id;
            return this;
        }

        public InventoryAlertReconstructor inventoryId(InventoryId inventoryId) {
            this.inventoryId = inventoryId;
            return this;
        }

        public InventoryAlertReconstructor alertType(AlertType alertType) {
            this.alertType = alertType;
            return this;
        }

        public InventoryAlertReconstructor severity(AlertSeverity severity) {
            this.severity = severity;
            return this;
        }

        public InventoryAlertReconstructor message(String message) {
            this.message = message;
            return this;
        }

        public InventoryAlertReconstructor status(AlertStatus status) {
            this.status = status;
            return this;
        }

        public InventoryAlertReconstructor triggeredAt(LocalDateTime triggeredAt) {
            this.triggeredAt = triggeredAt;
            return this;
        }

        public InventoryAlertReconstructor resolvedAt(LocalDateTime resolvedAt) {
            this.resolvedAt = resolvedAt;
            return this;
        }

        public InventoryAlertReconstructor resolvedBy(UserId resolvedBy) {
            this.resolvedBy = resolvedBy;
            return this;
        }

        public InventoryAlertReconstructor resolutionNotes(String resolutionNotes) {
            this.resolutionNotes = resolutionNotes;
            return this;
        }

        public InventoryAlertReconstructor createdAt(LocalDateTime createdAt) {
            this.createdAt = createdAt;
            return this;
        }

        public InventoryAlert reconstruct() {
            return new InventoryAlert(id, inventoryId, alertType, severity, message, status,
                    triggeredAt, resolvedAt, resolvedBy, resolutionNotes, createdAt);
        }
    }
}
