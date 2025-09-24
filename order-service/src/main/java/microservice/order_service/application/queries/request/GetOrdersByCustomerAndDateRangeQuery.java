package microservice.order_service.application.queries.request;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class GetOrdersByCustomerAndDateRangeQuery {
    private String customerId;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private int page;
    private int size;
}

