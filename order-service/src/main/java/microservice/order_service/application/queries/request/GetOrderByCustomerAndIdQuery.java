package microservice.order_service.application.queries.request;

import lombok.Builder;
import lombok.Data;

import java.util.UUID;

@Data
@Builder
public class GetOrderByCustomerAndIdQuery {
    private String customerId;
    private String orderId;
}

