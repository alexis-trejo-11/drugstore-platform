package microservice.user_service.utils.page;

public record SortInput(String sortBy, SortDirection sortDirection) {
    public enum SortDirection {
        ASC, DESC
    }

    public static SortInput defaultSort() {
        return new SortInput("id", SortDirection.ASC);
    }

    public String toJson() {
        return String.format("{\"sortBy\":\"%s\",\"sortDirection\":\"%s\"}", sortBy, sortDirection);
    }
}
