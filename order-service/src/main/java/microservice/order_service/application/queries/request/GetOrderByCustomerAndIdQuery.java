package microservice.order_service.application.queries.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GetOrderByCustomerAndIdQuery {
    private String customerId;
    private String orderId;
}

