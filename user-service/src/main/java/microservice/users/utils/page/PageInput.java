package microservice.users.utils.page;

public record PageInput(int page, int size, SortInput sortInput) {
    public PageInput {
        if (page < 0) {
            throw new IllegalArgumentException("Page number must not be less than zero.");
        }
        if (size <= 0) {
            throw new IllegalArgumentException("Page size must be greater than zero.");
        }
        if (sortInput == null) {
            throw new IllegalArgumentException("Sort input must not be null.");
        }
    }

    public static PageInput defaultPageInput() {
        return new PageInput(0, 10, SortInput.defaultSort());
    }
}

