package microservice.cart_service.app.cart.core.application.queries;

import microservice.cart_service.app.cart.core.domain.specficication.CartSearchCriteria;
import org.springframework.data.domain.Pageable;

public record SearchCartsQuery(CartSearchCriteria criteria, Pageable pageable) {
	public SearchCartsQuery {
			if (pageable == null) {
				Pageable.unpaged();
			}
			if (criteria == null) {
				criteria = CartSearchCriteria.defaultCriteria();
			}
	}
}
