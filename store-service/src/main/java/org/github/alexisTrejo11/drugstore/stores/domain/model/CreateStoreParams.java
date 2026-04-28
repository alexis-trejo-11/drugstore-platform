package org.github.alexisTrejo11.drugstore.stores.domain.model;

import lombok.Builder;
import org.github.alexisTrejo11.drugstore.stores.domain.model.schedule.StoreSchedule;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.ContactInfo;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreName;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Address;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Geolocation;

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
