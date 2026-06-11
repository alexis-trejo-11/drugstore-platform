package io.github.alexisTrejo11.drugstore.stores.domain.model;

import io.github.alexisTrejo11.drugstore.stores.domain.model.schedule.StoreSchedule;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.*;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.*;

import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Address;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.location.Geolocation;
import io.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;

public record ReconstructParams(
		StoreID id,
		StoreCode code,
		StoreName name,
		StoreStatus status,
		ContactInfo contactInfo,
		Address address,
		Geolocation geolocation,
		StoreSchedule serviceSchedule,
		StoreTimeStamps timeStamps) {
}
