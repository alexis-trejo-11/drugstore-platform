package io.github.alexisTrejo11.drugstore.order.orders.presentation.dto.request;

import libs_kernel.page.PageRequest;

import io.github.alexisTrejo11.drugstore.order.orders.application.query.request.GetOrdersByUserIDQuery;
import io.github.alexisTrejo11.drugstore.order.orders.domain.models.valueobjects.UserID;
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
