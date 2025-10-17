package microservice.store_service.domain.model.valueobjects.schedule;

import java.time.LocalTime;

public record TimeRange(
        LocalTime openingTime,
        LocalTime closingTime
) {

    public LocalTime getStart() {
        return openingTime;
    }

    public LocalTime getEnd() {
        return closingTime;
    }

    public boolean contains(LocalTime time) {
        return !time.isBefore(openingTime) && !time.isAfter(closingTime);
    }
}
