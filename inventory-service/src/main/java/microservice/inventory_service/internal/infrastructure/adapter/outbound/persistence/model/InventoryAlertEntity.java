package microservice.inventory_service.internal.infrastructure.adapter.outbound.persistence.model;

import lombok.*;

import jakarta.persistence.*;
import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertSeverity;
import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertStatus;
import microservice.inventory_service.internal.core.alert.domain.entity.valueobject.AlertType;

import java.time.LocalDateTime;

@Entity
@Table(name = "inventory_alerts")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryAlertEntity {

    @Id
    @Column(name = "id", length = 36)
    private String id;

    @Column(name = "inventory_id", nullable = false, length = 36)
    private String inventoryId;

    @Enumerated(EnumType.STRING)
    @Column(name = "alert_type", nullable = false)
    private AlertType alertType;

    @Enumerated(EnumType.STRING)
    @Column(name = "severity", nullable = false)
    private AlertSeverity severity;

    @Column(name = "message", columnDefinition = "TEXT", nullable = false)
    private String message;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private AlertStatus status;

    @Column(name = "triggered_at", nullable = false)
    private LocalDateTime triggeredAt;

    @Column(name = "resolved_at")
    private LocalDateTime resolvedAt;

    @Column(name = "resolved_by", length = 36)
    private String resolvedBy;

    @Column(name = "resolution_notes", columnDefinition = "TEXT")
    private String resolutionNotes;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
    }
}
