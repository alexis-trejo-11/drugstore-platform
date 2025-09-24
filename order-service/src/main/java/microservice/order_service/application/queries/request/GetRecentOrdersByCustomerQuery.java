package microservice.order_service.application.queries.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetRecentOrdersByCustomerQuery {
    private String customerId;
    private int limit;
}
