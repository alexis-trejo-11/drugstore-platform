package microservice.order_service.orders.infrastructure.api.controller.dto;

import libs_kernel.page.PageInput;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import microservice.order_service.orders.application.queries.request.GetOrdersByUserIDQuery;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetUserOrdersRequest {
    @Length(min = 3, max = 50, message = "Status must be between 3 and 50 characters")
    private String status;

    private PageInput pagination = PageInput.defaultPageInput();

    public GetOrdersByUserIDQuery toQuery(String customerId) {
        return new GetOrdersByUserIDQuery(
                UserID.of(customerId),
                pagination
        );
    }
}
