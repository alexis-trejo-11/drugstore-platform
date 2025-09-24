package microservice.order_service.application.queries.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GetOrdersByCustomerQuery {
    private String customerId;
    private int page;
    private int size;
}

