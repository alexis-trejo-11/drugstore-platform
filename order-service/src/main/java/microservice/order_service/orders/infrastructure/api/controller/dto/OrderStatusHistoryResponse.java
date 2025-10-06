package microservice.order_service.orders.infrastructure.api.controller.dto;

import microservice.order_service.orders.domain.models.enums.OrderStatus;
import java.time.LocalDateTime;

public record OrderStatusHistoryResponse(
        OrderStatus status,
        LocalDateTime timestamp,
        String changedByRole,
        String reason,
        String notes,

        String pharmacistId,
        String prescriptionReference,
        Boolean requiresPrescriptionValidation,
        LocalDateTime estimatedCompletionTime
) {


    public static OrderStatusHistoryResponse forCustomerAction(
            OrderStatus status, LocalDateTime timestamp, String reason) {
        return new OrderStatusHistoryResponse(
                status, timestamp, "customer", reason, null,
                null, null, null, null
        );
    }

    public static OrderStatusHistoryResponse forPharmacistAction(
            OrderStatus status, LocalDateTime timestamp,
            String pharmacistId, String reason, String notes) {
        return new OrderStatusHistoryResponse(
                status, timestamp, "pharmacist", reason, notes,
                pharmacistId, null, null, null
        );
    }

    public static OrderStatusHistoryResponse forPrescriptionValidation(
            OrderStatus status, LocalDateTime timestamp,
            String pharmacistId, String prescriptionReference,
            boolean requiresValidation, LocalDateTime estimatedCompletion) {
        return new OrderStatusHistoryResponse(
                status, timestamp, "pharmacist",
                "Prescription validated", "Medical prescription review completed",
                pharmacistId, prescriptionReference, requiresValidation, estimatedCompletion
        );
    }

    public static OrderStatusHistoryResponse forSystemAction(
            OrderStatus status, LocalDateTime timestamp, String reason) {
        return new OrderStatusHistoryResponse(
                status, timestamp, "system", reason, null,
                null, null, null, null
        );
    }

    public boolean isPharmacistAction() {
        return "pharmacist".equals(changedByRole);
    }

    public boolean requiresPrescriptionCheck() {
        return Boolean.TRUE.equals(requiresPrescriptionValidation);
    }
}