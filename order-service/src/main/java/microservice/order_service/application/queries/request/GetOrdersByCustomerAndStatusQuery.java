package microservice.order_service.application.queries.request;

import libs_kernel.page.PageInput;
import lombok.Builder;
import lombok.Data;
import microservice.order_service.domain.models.enums.OrderStatus;
import microservice.order_service.domain.models.valueobjects.CustomerID;

@Data
@Builder
public class GetOrdersByCustomerAndStatusQuery {
    private CustomerID customerId;
    private OrderStatus status;
    PageInput pagination;
}
