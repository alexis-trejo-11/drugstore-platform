package libs_kernel.page;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

@Getter
@Setter
public class PaginationMetadata {
    public long totalElements;
    public int totalPages;
    public int currentPage;
    public int pageSize;
    public boolean isFirst;
    public boolean isLast;
    public boolean hasNext;
    public boolean hasPrevious;

    public PaginationMetadata(long totalElements, int totalPages, int currentPage, int pageSize, boolean isFirst, boolean isLast, boolean hasNext, boolean hasPrevious) {
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.isFirst = isFirst;
        this.isLast = isLast;
        this.hasNext = hasNext;
        this.hasPrevious = hasPrevious;
    }

    public PaginationMetadata(long totalElements, int currentPage, int pageSize) {
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements cannot be negative");
        }

        if (currentPage <= 0) {
            throw new IllegalArgumentException("currentPage must be greater than zero");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than zero");
        }

        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = (int) Math.ceil((double) totalElements / pageSize);
        this.isFirst = currentPage == 1;
        this.isLast = pageSize >= totalPages;
        this.hasNext = pageSize < totalPages;
        this.hasPrevious = pageSize > 1;
    }

    public static PaginationMetadata from(Page<?> page) {
        return new PaginationMetadata(
                page.getTotalElements(),
                page.getTotalPages(),
                page.getNumber() + 1, // Page 1-based index
                page.getSize(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious()
        );
    }

    public static PaginationMetadata empty() {
        return new PaginationMetadata(0, 1, 1);
    }
}
