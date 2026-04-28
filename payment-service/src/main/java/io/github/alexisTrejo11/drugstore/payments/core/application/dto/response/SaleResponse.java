package io.github.alexisTrejo11.drugstore.payments.core.application.dto.response;

import io.github.alexisTrejo11.drugstore.payments.core.domain.model.Sale;

public record SaleResponse() {

	public static SaleResponse from(Sale saleDTO) {
		return new SaleResponse();
	}
}
