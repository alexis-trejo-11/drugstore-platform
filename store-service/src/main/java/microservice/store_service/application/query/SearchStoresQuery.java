package microservice.store_service.application.query;

import lombok.Builder;
import lombok.Getter;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.specification.StoreSearchCriteria;

import java.util.Set;


@Builder
public record SearchStoresQuery(
        String nameLike,
        String phoneLike,
        String emailLike,
        String exactCode,
        String country,
        String state,
        String neighborhood,
        Set<StoreStatus> statuses,
        StoreSearchCriteria.LocationFilter locationFilter,
        StoreSearchCriteria.ScheduleFilter scheduleFilter,
        Integer page,
        Integer size,
        StoreSearchCriteria.SortCriteria sortBy
) {
    public StoreSearchCriteria toCriteria() {
        return StoreSearchCriteria.builder()
                .nameLike(nameLike)
                .phoneLike(phoneLike)
                .emailLike(emailLike)
                .exactCode(exactCode)
                .country(country)
                .state(state)
                .neighborhood(neighborhood)
                .statuses(statuses)
                .locationFilter(locationFilter)
                .scheduleFilter(scheduleFilter)
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .build();
    }
}
