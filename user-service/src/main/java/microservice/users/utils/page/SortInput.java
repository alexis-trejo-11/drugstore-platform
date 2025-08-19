package microservice.users.utils.page;

public record SortInput(String sortBy, SortDirection sortDirection) {
    public enum SortDirection {
        ASC, DESC
    }

    public static SortInput defaultSort() {
        return new SortInput("id", SortDirection.ASC);
    }
}
