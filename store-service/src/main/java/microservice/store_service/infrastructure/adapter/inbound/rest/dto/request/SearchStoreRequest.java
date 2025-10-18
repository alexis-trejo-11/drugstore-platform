package microservice.store_service.infrastructure.adapter.inbound.rest.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import libs_kernel.page.PageInput;
import libs_kernel.page.PageableResponse;
import microservice.store_service.application.query.SearchStoresQuery;
import microservice.store_service.domain.model.enums.StoreStatus;
import microservice.store_service.domain.specification.StoreSearchCriteria;

import java.util.Set;

@Schema(description = "Request DTO for searching stores based on various criteria.")
public record SearchStoreRequest(
        @Schema(description = "Partial match for store name", example = "Drugstore Pharma Plus")
        String nameLike,

        @Schema(description = "Partial match for store phone number", example = "+1-202-555")
        String phoneLike,

        @Schema(description = "Partial match for store email", example = "drugstore123@pharmacy.com")
        String emailLike,

        @Schema(description = "Exact match for store code", example = "STORE12345")
        String exactCode,

        @Schema(description = "Country where the store is located", example = "USA")
        String country,

        @Schema(description = "State where the store is located", example = "California")
        String state,

        @Schema(description = "Neighborhood where the store is located", example = "Downtown")
        String neighborhood,

        @Schema(description = "Set of store statuses to filter by", example = "[ACTIVE, INACTIVE]")
        Set<StoreStatus>statuses,

        StoreSearchCriteria.LocationFilter locationFilter,
        StoreSearchCriteria.ScheduleFilter scheduleFilter,
        PageInput pageInput
) {
    public SearchStoresQuery toQuery() {
        var sortCriteria = StoreSearchCriteria.SortCriteria.DISTANCE_ASC; // Default sort criteria
        return SearchStoresQuery.builder()
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
                .page(pageInput.page())
                .size(pageInput.size())
                .sortBy(sortCriteria)
                .build();
    }
}
