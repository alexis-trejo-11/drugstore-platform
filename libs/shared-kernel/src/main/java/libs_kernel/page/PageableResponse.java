package libs_kernel.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Data
public class PageableResponse<T> implements PageResponse<T> {
    private List<T> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
    private boolean first;
    private boolean last;
    private boolean hasNext;
    private boolean hasPrevious;

    @Builder
    public PageableResponse(List<T> content, int page, int size, long totalElements, int totalPages,
                           boolean first, boolean last, boolean hasNext, boolean hasPrevious) {
        this.content = content;
        this.page = page;
        this.size = size;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.first = first;
        this.last = last;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageableResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public PageableResponse(List<T> items, int page, int size, long totalItems) {
        this.content = items;
        this.page = page;
        this.size = size;
        this.totalElements = totalItems;
        this.totalPages = (int) Math.ceil((double) totalItems / size);
        this.first = page == 1;
        this.last = page >= totalPages;
        this.hasNext = page < totalPages;
        this.hasPrevious = page > 1;
    }

    public static <T> PageResponse<T> empty(PageInput pageInput) {
        return new PageableResponse<>(
                List.of(),
                pageInput.page(),
                pageInput.size(),
                0,
                0,
                true,
                true,
                false,
                false);
    }

    public static <T> PageResponse<T> empty() {
        return new PageableResponse<>(
                List.of(),
                1,
                0,
                0,
                1,
                true,
                true,
                false,
                false);
    }

    @Override
    public PageResponse<T> fromPage(Page<T> page) {
        return new PageableResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    // Getters
    @Override
    @JsonProperty("content")
    public List<T> content() {
        return content;
    }

    @Override
    @JsonProperty("page")
    public int page() {
        return page;
    }

    @Override
    @JsonProperty("size")
    public int size() {
        return size;
    }

    @Override
    @JsonProperty("totalElements")
    public long totalElements() {
        return totalElements;
    }

    @Override
    @JsonProperty("totalPages")
    public int totalPages() {
        return totalPages;
    }

    @Override
    @JsonProperty("first")
    public boolean first() {
        return first;
    }

    @Override
    @JsonProperty("last")
    public boolean last() {
        return last;
    }

    @Override
    @JsonProperty("hasNext")
    public boolean hasNext() {
        return hasNext;
    }

    @Override
    @JsonProperty("hasPrevious")
    public boolean hasPrevious() {
        return hasPrevious;
    }
}