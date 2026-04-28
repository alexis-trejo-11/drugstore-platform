package microservice.order_service.orders.infrastructure.api.dto.request;

import libs_kernel.page.PageRequest;

import microservice.order_service.orders.application.queries.request.GetOrdersByUserIDQuery;
import microservice.order_service.orders.domain.models.valueobjects.UserID;
import org.hibernate.validator.constraints.Length;

public record GetUserOrdersRequest(
		@Length(min = 3, max = 50)
		String status,
		PageRequest pageRequest
) {
	public GetOrdersByUserIDQuery toQuery(String customerId) {
		return new GetOrdersByUserIDQuery(
				UserID.of(customerId),
				pageRequest != null ? pageRequest.toPageable() : PageRequest.defaultPageRequest().toPageable()
		);
	}
}
