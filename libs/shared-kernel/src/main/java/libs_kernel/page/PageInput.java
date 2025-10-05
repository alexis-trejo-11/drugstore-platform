package libs_kernel.page;

import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

public record PageInput(int page, int size, SortInput sortInput) {

    public PageInput {
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

    public static PageInput defaultPageInput() {
        return new PageInput(1, 10, SortInput.defaultSort());
    }

    public static PageInput of(int page, int size) {
        return new PageInput(page, size, SortInput.defaultSort());
    }

    public static PageInput of(int page, int size, String sortBy, String direction) {
        return new PageInput(page, size, new SortInput(sortBy, direction));
    }

    public Pageable toPageable() {
        return PageRequest.of(page, size, sortInput.toSort());
    }

    public String toJson() {
        return String.format("{\"page\": %d, \"size\": %d, \"sortInput\": %s}",
                page, size, sortInput.toJson());
    }

    public PageInput next() {
        return new PageInput(page + 1, size, sortInput);
    }

    public PageInput previous() {
        if (page == 0)
            return this;
        return new PageInput(page - 1, size, sortInput);
    }

    public PageInput withSize(int newSize) {
        return new PageInput(page, newSize, sortInput);
    }

    public PageInput withSort(SortInput newSort) {
        return new PageInput(page, size, newSort);
    }
}
