package microservice.store_service.app.domain.specification;

import lombok.Builder;
import microservice.store_service.app.domain.model.enums.StoreStatus;
import microservice.store_service.app.domain.model.valueobjects.location.Geolocation;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.util.Set;

@Builder
public record StoreSearchCriteria(
        String nameLike,
        String phoneLike,
        String emailLike,
        String exactCode,
        String country,
        String state,
        String neighborhood,
        Set<StoreStatus> statuses,

        LocationFilter locationFilter,
        ScheduleFilter scheduleFilter,
        Integer page,
        Integer size,
        SortCriteria sortBy
) {


    public boolean hasScheduleFilter() {
        return scheduleFilter != null;
    }

    public static record LocationFilter(Geolocation center, Double radiusKm, String country, String state, String city,
                                        String zipCode) {
        public boolean hasProximitySearch() {
            return center != null && radiusKm != null;
        }

        public boolean hasGeographicFilter() {
            return country != null || state != null || city != null || zipCode != null;
        }

        public static LocationFilterBuilder builder() { return new LocationFilterBuilder(); }

        public static final class LocationFilterBuilder {
            private Geolocation center;
            private Double radiusKm;
            private String country;
            private String state;
            private String city;
            private String zipCode;

            public LocationFilterBuilder center(Geolocation center) { this.center = center; return this; }
            public LocationFilterBuilder radiusKm(Double radiusKm) { this.radiusKm = radiusKm; return this; }
            public LocationFilterBuilder country(String country) { this.country = country; return this; }
            public LocationFilterBuilder state(String state) { this.state = state; return this; }
            public LocationFilterBuilder city(String city) { this.city = city; return this; }
            public LocationFilterBuilder zipCode(String zipCode) { this.zipCode = zipCode; return this; }

            public LocationFilter build() { return new LocationFilter(center, radiusKm, country, state, city, zipCode); }
        }
    }

    public static record ScheduleFilter(Boolean is24Hours, Boolean isOpenNow, LocalDateTime isOpenAt, DayOfWeek dayOfWeek,
                                        Set<DayOfWeek> closedDays) {
        public boolean needsScheduleEvaluation() {
            return isOpenNow != null || isOpenAt != null;
        }

        public static ScheduleFilterBuilder builder() { return new ScheduleFilterBuilder(); }

        public static final class ScheduleFilterBuilder {
            private Boolean is24Hours;
            private Boolean isOpenNow;
            private LocalDateTime isOpenAt;
            private DayOfWeek dayOfWeek;
            private Set<DayOfWeek> closedDays;

            public ScheduleFilterBuilder is24Hours(Boolean is24Hours) { this.is24Hours = is24Hours; return this; }
            public ScheduleFilterBuilder isOpenNow(Boolean isOpenNow) { this.isOpenNow = isOpenNow; return this; }
            public ScheduleFilterBuilder isOpenAt(LocalDateTime isOpenAt) { this.isOpenAt = isOpenAt; return this; }
            public ScheduleFilterBuilder dayOfWeek(DayOfWeek dayOfWeek) { this.dayOfWeek = dayOfWeek; return this; }
            public ScheduleFilterBuilder closedDays(Set<DayOfWeek> closedDays) { this.closedDays = closedDays; return this; }

            public ScheduleFilter build() { return new ScheduleFilter(is24Hours, isOpenNow, isOpenAt, dayOfWeek, closedDays); }
        }
    }

    public enum SortCriteria {
        CODE_ASC("code", "ASC"),
        CODE_DESC("code", "DESC"),
        NAME_ASC("name", "ASC"),
        NAME_DESC("name", "DESC"),
        CREATED_AT_ASC("createdAt", "ASC"),
        CREATED_AT_DESC("createdAt", "DESC");
        //DISTANCE_ASC("distance", "ASC"),
        //DISTANCE_DESC("distance", "DESC");

        protected final String field;
        protected final String direction;

        SortCriteria(String field, String direction) {
            this.field = field;
            this.direction = direction;
        }

        public String field() { return field; }
        public String direction() { return direction; }
    }

    // Common search criteria presets
    public static StoreSearchCriteria findAll() {
        return StoreSearchCriteria.builder()
                .page(0)
                .size(20)
                .sortBy(SortCriteria.NAME_ASC)
                .build();
    }

    public static StoreSearchCriteria findActiveStores() {
        return StoreSearchCriteria.builder()
                .statuses(Set.of(StoreStatus.ACTIVE))
                .build();
    }

    public static StoreSearchCriteria findByCode(String code) {
        return StoreSearchCriteria.builder()
                .exactCode(code)
                .build();
    }

    public static StoreSearchCriteria findByStatus(StoreStatus status) {
        return StoreSearchCriteria.builder()
                .statuses(Set.of(status))
                .build();
    }

    public static StoreSearchCriteria findByStatus(StoreStatus status, int page, int size, SortCriteria sortBy) {
        return StoreSearchCriteria.builder()
                .statuses(Set.of(status))
                .page(page)
                .size(size)
                .sortBy(sortBy)
                .build();
    }

    public static StoreSearchCriteria findNearby(Geolocation center, double radiusKm) {
        return StoreSearchCriteria.builder()
                .locationFilter(LocationFilter.builder()
                        .center(center)
                        .radiusKm(radiusKm)
                        .build())
                .statuses(Set.of(StoreStatus.ACTIVE))
                .sortBy(SortCriteria.NAME_ASC)
                .build();
    }

    public static StoreSearchCriteria findOpenNow() {
        return StoreSearchCriteria.builder()
                .statuses(Set.of(StoreStatus.ACTIVE))
                .scheduleFilter(ScheduleFilter.builder()
                        .isOpenNow(true)
                        .build())
                .build();
    }

    // Optional: validate non-null invariants or normalize fields in compact constructor
    public StoreSearchCriteria {
        // normalize empty strings to null, if desired (example)
        if (nameLike != null && nameLike.isBlank()) nameLike = null;
        if (phoneLike != null && phoneLike.isBlank()) phoneLike = null;
        if (emailLike != null && emailLike.isBlank()) emailLike = null;
        if (exactCode != null && exactCode.isBlank()) exactCode = null;
    }

    @Override
    public String toString() {
        return "StoreSearchCriteria[" +
                "nameLike=" + nameLike +
                ", phoneLike=" + phoneLike +
                ", emailLike=" + emailLike +
                ", exactCode=" + exactCode +
                ", country=" + country +
                ", state=" + state +
                ", neighborhood=" + neighborhood +
                ", statuses=" + statuses +
                ", locationFilter=" + locationFilter +
                ", scheduleFilter=" + scheduleFilter +
                ", page=" + page +
                ", size=" + size +
                ", sortBy=" + sortBy +
                ']';
    }
}
