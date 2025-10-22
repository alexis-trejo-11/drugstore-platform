package libs_kernel.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record Pagination(int page, int size, SortInput sortInput) {

    public Pagination {
        if (page < 1) {
            throw new IllegalArgumentException("Page number must not be less than 1.");
        }
        if (size <= 0 || size > 1000) {
            throw new IllegalArgumentException("Page size must be between 1 and 1000.");
        }
        if (sortInput == null) {
            sortInput = SortInput.defaultSort();
        }
    }

    public static Pagination defaultPageInput() {
        return new Pagination(1, 10, SortInput.defaultSort());
    }

    public static Pagination of(int page, int size) {
        return new Pagination(page, size, SortInput.defaultSort());
    }

    public static Pagination of(int page, int size, String sortBy, String direction) {
        return new Pagination(page, size, new SortInput(sortBy, direction));
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, sortInput.toSort());
    }

    public String toJson() {
        return String.format("{\"page\": %d, \"size\": %d, \"sortInput\": %s}",
                page, size, sortInput.toJson());
    }

    public Pagination next() {
        return new Pagination(page + 1, size, sortInput);
    }

    public Pagination previous() {
        if (page == 0)
            return this;
        return new Pagination(page - 1, size, sortInput);
    }

    public Pagination withSize(int newSize) {
        return new Pagination(page, newSize, sortInput);
    }

    public Pagination withSort(SortInput newSort) {
        return new Pagination(page, size, newSort);
    }
}
