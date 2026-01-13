package microservice.store_service.app.domain.model;

import lombok.Builder;
import microservice.store_service.app.domain.model.schedule.StoreSchedule;
import microservice.store_service.app.domain.model.valueobjects.ContactInfo;
import microservice.store_service.app.domain.model.valueobjects.StoreCode;
import microservice.store_service.app.domain.model.valueobjects.StoreName;
import microservice.store_service.app.domain.model.valueobjects.location.Address;
import microservice.store_service.app.domain.model.valueobjects.location.Geolocation;

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
