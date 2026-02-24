package io.github.alexisTrejo11.drugstore.payments.core.domain.model.params;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.SaleStatus;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.SaleID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.SaleTimeStamps;

/**
 * Parameters for reconstructing a Sale from persistence.
 */
public record ReconstructSaleParams(
    SaleID id,
    PaymentID paymentId,
    OrderID orderId,
    CustomerID customerId,
    Money totalAmount,
    SaleStatus status,
    Money refundedAmount,
    SaleTimeStamps timeStamps) {
}
