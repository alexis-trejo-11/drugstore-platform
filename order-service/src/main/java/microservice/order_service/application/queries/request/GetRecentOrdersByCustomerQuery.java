package microservice.order_service.application.queries.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
public class GetRecentOrdersByCustomerQuery {
    private String customerId;
    private int limit;
}
