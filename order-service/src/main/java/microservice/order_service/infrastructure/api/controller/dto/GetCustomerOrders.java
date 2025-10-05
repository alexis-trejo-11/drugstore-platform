package microservice.order_service.infrastructure.api.controller.dto;

import libs_kernel.page.PageInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.order_service.application.queries.request.GetOrdersByCustomerQuery;
import microservice.order_service.domain.models.valueobjects.CustomerID;
import org.hibernate.validator.constraints.Length;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerOrders {
    @Length(min = 3, max = 50, message = "Status must be between 3 and 50 characters")
    private String status;

    private PageInput pagination = PageInput.defaultPageInput();

    public GetOrdersByCustomerQuery toQuery(String customerId) {
        return new GetOrdersByCustomerQuery(
                CustomerID.of(customerId),
                pagination
        );
    }
}
