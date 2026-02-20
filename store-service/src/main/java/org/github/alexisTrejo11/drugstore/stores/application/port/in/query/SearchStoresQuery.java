package org.github.alexisTrejo11.drugstore.stores.application.port.in.query;

import lombok.Builder;
import org.github.alexisTrejo11.drugstore.stores.domain.model.enums.StoreStatus;
import org.github.alexisTrejo11.drugstore.stores.domain.specification.StoreSearchCriteria;
import org.springframework.data.domain.Pageable;

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

    @Override
    public String toString() {
        return "SearchStoresQuery{" +
                "nameLike='" + nameLike + '\'' +
                ", phoneLike='" + phoneLike + '\'' +
                ", emailLike='" + emailLike + '\'' +
                ", exactCode='" + exactCode + '\'' +
                ", country='" + country + '\'' +
                ", state='" + state + '\'' +
                ", neighborhood='" + neighborhood + '\'' +
                ", statuses=" + statuses +
                ", locationFilter=" + locationFilter +
                ", scheduleFilter=" + scheduleFilter +
                ", page=" + page +
                ", size=" + size +
                ", sortBy=" + sortBy +
                '}';
    }

    public Pageable toPageable() {
        return Pageable.ofSize(size != null ? size : 10).withPage(page != null ? page - 1 : 0);
    }
}
