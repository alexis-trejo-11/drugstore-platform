package microservice.order_service.application.queries.request;

import lombok.Builder;
import lombok.Data;
import microservice.order_service.domain.models.valueobjects.CustomerId;

import java.util.UUID;

@Data
@Builder
public class GetOrdersByCustomerAndStatusQuery {
    private String customerId;
    private String status;
    private int page;
    private int size;
}
