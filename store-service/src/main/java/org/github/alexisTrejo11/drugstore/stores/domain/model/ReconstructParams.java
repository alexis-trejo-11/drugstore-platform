package org.github.alexisTrejo11.drugstore.stores.domain.model;

import org.github.alexisTrejo11.drugstore.stores.domain.model.schedule.StoreSchedule;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.*;

import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Address;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Geolocation;
import org.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;

public record ReconstructParams(
		StoreID id,
		StoreCode code,
		StoreName name,
		StoreStatus status,
		ContactInfo contactInfo,
		Address address,
		Geolocation geolocation,
		StoreSchedule serviceSchedule,
		StoreTimeStamps timeStamps
) {
}
