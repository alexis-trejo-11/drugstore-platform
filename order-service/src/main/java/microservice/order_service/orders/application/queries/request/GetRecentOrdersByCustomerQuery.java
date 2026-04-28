package microservice.order_service.orders.application.queries.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import microservice.order_service.orders.domain.models.valueobjects.UserID;

@Data
@AllArgsConstructor
public class GetRecentOrdersByCustomerQuery {
    private UserID customerId;
    private int limit;
}
