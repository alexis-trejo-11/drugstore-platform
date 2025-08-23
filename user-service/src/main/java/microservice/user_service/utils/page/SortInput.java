package microservice.user_service.utils.page;

import org.springframework.data.domain.Sort;

public record SortInput(String sortBy, String direction) {

    public SortInput {
        if (sortBy == null || sortBy.trim().isEmpty()) {
            sortBy = "createdAt";
        }
        if (direction == null || direction.trim().isEmpty()) {
            direction = "desc";
        }

        direction = direction.toUpperCase();
        if (!direction.equals("ASC") && !direction.equals("DESC")) {
            throw new IllegalArgumentException("Direction must be 'ASC' or 'DESC'");
        }
    }

    public static SortInput defaultSort() {
        return new SortInput("createdAt", "desc");
    }

    public static SortInput of(String sortBy) {
        return new SortInput(sortBy, "desc");
    }

    public static SortInput of(String sortBy, String direction) {
        return new SortInput(sortBy, direction);
    }

    public Sort toSort() {
        Sort.Direction sortDirection = direction.equalsIgnoreCase("ASC")
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(sortDirection, sortBy);
    }

    public String toJson() {
        return String.format("{\"sortBy\": \"%s\", \"direction\": \"%s\"}", sortBy, direction);
    }

    public SortInput withDirection(String newDirection) {
        return new SortInput(sortBy, newDirection);
    }

    public SortInput withSortBy(String newSortBy) {
        return new SortInput(newSortBy, direction);
    }
}