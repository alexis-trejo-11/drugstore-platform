package microservice.user_service.utils.page;

public record PaginationMetadata(
        long totalElements,
        int totalPages,
        int currentPage,
        int pageSize) {
    public PaginationMetadata {
        if (totalElements < 0) {
            throw new IllegalArgumentException("totalElements cannot be negative");
        }
        if (totalPages < 0) {
            throw new IllegalArgumentException("totalPages cannot be negative");
        }
        if (currentPage <= 0) {
            throw new IllegalArgumentException("currentPage must be greater than zero");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("pageSize must be greater than zero");
        }
    }

    public static PaginationMetadata from(PageResponse<?> pageResponse) {
        return new PaginationMetadata(
                pageResponse.getTotalElements(),
                pageResponse.getTotalPages(),
                pageResponse.getPage(),
                pageResponse.getSize());
    }

    public static PaginationMetadata empty() {
        return new PaginationMetadata(0, 0, 1, 1);
    }
}
