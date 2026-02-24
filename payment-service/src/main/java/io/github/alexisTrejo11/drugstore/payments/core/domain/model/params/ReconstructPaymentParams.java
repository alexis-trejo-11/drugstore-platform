package io.github.alexisTrejo11.drugstore.payments.core.domain.model.params;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentMethod;
import io.github.alexisTrejo11.drugstore.payments.core.domain.model.enums.PaymentStatus;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.CustomerID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.Money;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.OrderID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentGatewayRef;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentID;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.PaymentTimeStamps;
import io.github.alexisTrejo11.drugstore.payments.core.domain.valueobjects.RefundInfo;

/**
 * Parameters for reconstructing a Payment from persistence.
 */
public record ReconstructPaymentParams(
    PaymentID id,
    OrderID orderId,
    CustomerID customerId,
    Money amount,
    PaymentMethod paymentMethod,
    PaymentStatus status,
    PaymentGatewayRef gatewayRef,
    Money refundedAmount,
    RefundInfo refundInfo,
    PaymentTimeStamps timeStamps) {
}
