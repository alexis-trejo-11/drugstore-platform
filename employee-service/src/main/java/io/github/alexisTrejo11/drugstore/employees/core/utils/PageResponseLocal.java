package io.github.alexisTrejo11.drugstore.employees.core.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;
import org.springframework.data.domain.Page;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@Data
public class PageResponseLocal<T> {
    private List<T> content;
    private PaginationMetadata paginationMetadata;

    @Builder
    public PageResponseLocal(List<T> content, int page, int size, long totalElements) {
        this.content = content;
        this.paginationMetadata = new PaginationMetadata(totalElements, page, size);
    }

    public PageResponseLocal(List<T> content, PaginationMetadata paginationMetadata) {
        this.content = content;
        this.paginationMetadata = paginationMetadata;
    }

    public static <T> PageResponseLocal<T> from(Page<T> page) {
        return new PageResponseLocal<>(
                page.getContent(),
                PaginationMetadata.from(page)
        );
    }

    public static <T> PageResponseLocal<T> empty() {
        return new PageResponseLocal<>(
                List.of(),
                PaginationMetadata.empty());
    }

    public PageResponseLocal<T> fromPage(Page<T> page) {
        return new PageResponseLocal<T>(
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