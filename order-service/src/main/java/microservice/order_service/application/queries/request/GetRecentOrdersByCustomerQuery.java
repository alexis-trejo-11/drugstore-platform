package microservice.order_service.application.queries.request;

import lombok.AllArgsConstructor;
import lombok.Data;
import microservice.order_service.domain.models.valueobjects.CustomerID;

@Data
@AllArgsConstructor
public class GetRecentOrdersByCustomerQuery {
    private CustomerID customerId;
    private int limit;
}
