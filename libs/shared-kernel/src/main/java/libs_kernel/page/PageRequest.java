package libs_kernel.page;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import org.springframework.data.domain.Pageable;

@Getter
public class PageRequest {
    @Min(1)
    private int page = 1;
    @Min(1)
    @Max(100)
    private int size = 10;
    private SortInput sortInput = SortInput.defaultSort();

    public static PageRequest of(int page, int size, SortInput sortInput) {
        return new PageRequest(page, size, sortInput);
    }

    public static PageRequest of(int page, int size, String sortBy, String direction) {
        return new PageRequest(page, size, new SortInput(sortBy, direction));
    }

    public PageRequest() {
    }

    public PageRequest(int page, int size, SortInput sortInput) {
        this.page = page;
        this.size = size;
        this.sortInput = sortInput;
    }

    public Pageable toPageable() {
        return Pageable.ofSize(size).withPage(page - 1);
    }
}
