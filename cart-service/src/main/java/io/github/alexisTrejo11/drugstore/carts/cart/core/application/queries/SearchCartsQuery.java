package io.github.alexisTrejo11.drugstore.carts.cart.core.application.queries;

import io.github.alexisTrejo11.drugstore.carts.cart.core.domain.specficication.CartSearchCriteria;
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
