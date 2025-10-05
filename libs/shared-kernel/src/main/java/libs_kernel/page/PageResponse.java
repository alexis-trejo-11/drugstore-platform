package libs_kernel.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PageResponse<T> {

    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
    private final boolean first;
    private final boolean last;
    private final boolean hasNext;
    private final boolean hasPrevious;


    public PageResponse(List<T> content, int page, int size, long totalElements,
            int totalPages, boolean first, boolean last,
            boolean hasNext, boolean hasPrevious) {
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
        return new PageResponse<>(
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
    @JsonProperty("content")
    public List<T> getContent() {
        return content;
    }

    @JsonProperty("page")
    public int getPage() {
        return page;
    }

    @JsonProperty("size")
    public int getSize() {
        return size;
    }

    @JsonProperty("totalElements")
    public long getTotalElements() {
        return totalElements;
    }

    @JsonProperty("totalPages")
    public int getTotalPages() {
        return totalPages;
    }

    @JsonProperty("first")
    public boolean isFirst() {
        return first;
    }

    @JsonProperty("last")
    public boolean isLast() {
        return last;
    }

    @JsonProperty("hasNext")
    public boolean hasNext() {
        return hasNext;
    }

    @JsonProperty("hasPrevious")
    public boolean hasPrevious() {
        return hasPrevious;
    }


    public static <T> PageResponse<T> empty(PageInput pageInput) {
        return new PageResponse<>(
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
        return new PageResponse<>(
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
}