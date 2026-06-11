package libs_kernel.page;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Data
public class PageResponse<T> {
    private List<T> content;
    private PaginationMetadata paginationMetadata;

    @Builder
    public PageResponse(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.paginationMetadata = new PaginationMetadata(totalElements, page, size);
    }

    public PageResponse(List<T> content, PaginationMetadata paginationMetadata) {
        this.content = content;
        this.paginationMetadata = paginationMetadata;
    }

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                PaginationMetadata.from(page)
        );
    }

    public static <T> PageResponse<T> empty() {
        return new PageResponse<>(
                List.of(),
                PaginationMetadata.empty());
    }

    public PageResponse<T> fromPage(Page<T> page) {
        return new PageResponse<T>(
                page.getContent(),
                PaginationMetadata.from(page)
        );
    }

    @JsonProperty("content")
    public List<T> getContent() {
        return content;
    }

    @JsonProperty("pagination_metadata")
    public PaginationMetadata getPaginationMetadata() {
        return paginationMetadata;
    }
}