package io.github.alexisTrejo11.drugstore.order.orders.application.query.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;

@Data
@AllArgsConstructor
public class GetRecentOrdersByCustomerQuery {
    private UserID customerId;
    private int limit;
}
