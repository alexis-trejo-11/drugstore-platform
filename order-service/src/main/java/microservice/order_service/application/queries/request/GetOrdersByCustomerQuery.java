package microservice.order_service.application.queries.request;

import libs_kernel.page.PageInput;
import microservice.order_service.domain.models.valueobjects.CustomerID;

import java.util.Objects;

public record GetOrdersByCustomerQuery(CustomerID customerId, PageInput pagination) {
    public GetOrdersByCustomerQuery {
        Objects.requireNonNull(customerId, "customerId must not be null");
        Objects.requireNonNull(pagination, "pagination must not be null");
    }
}
