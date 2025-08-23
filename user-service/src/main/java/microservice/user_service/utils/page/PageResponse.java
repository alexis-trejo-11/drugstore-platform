package microservice.user_service.utils.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

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

    // Metadata adicional opcional
    private Object metadata;

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

    @JsonProperty("metadata")
    public Object getMetadata() {
        return metadata;
    }

    public void setMetadata(Object metadata) {
        this.metadata = metadata;
    }

    // Métodos de conveniencia
    public PageResponse<T> withMetadata(Object metadata) {
        this.metadata = metadata;
        return this;
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