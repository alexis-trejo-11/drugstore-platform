package io.github.alexisTrejo11.drugstore.stores.domain.model;

import lombok.Builder;
import io.github.alexisTrejo11.drugstore.stores.domain.model.schedule.StoreSchedule;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.ContactInfo;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreName;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Address;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Geolocation;

@Builder
public record CreateStoreParams(
		StoreCode code,
		StoreName name,
		ContactInfo contactInfo,
		Address address,
		Geolocation geolocation,
		StoreSchedule serviceSchedule) {

		public CreateStoreParams {
			if (code == null) {
				throw new IllegalArgumentException("Store code cannot be null");
			}
			if (name == null) {
				throw new IllegalArgumentException("Store name cannot be null");
			}

		}
}
