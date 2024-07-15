package at.backend.drugstore.microservice.common_models.Models.Sales;
import lombok.Getter;

@Getter
public enum SaleStatus {
    PENDING_NO_INTEREST_INSTALLMENTS("Pending No Interest Installments"),
    PAID("Paid"),
    CANCELLED("Cancelled");

    private final String displayName;

    SaleStatus(String displayName) {
        this.displayName = displayName;
    }

}