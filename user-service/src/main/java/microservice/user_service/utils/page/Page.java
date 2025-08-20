package microservice.user_service.utils.page;


import java.util.List;

public record Page<T>(
    PaginationMetadata pageMetadata,
    SortInput sortInput,
    List<T> content
) {
    public Page {
       
        if (sortInput == null) {
            throw new IllegalArgumentException("Sort input must not be null.");
        }
    }

    public static <T> Page<T> empty() {
        return new Page<>(PaginationMetadata.empty(), SortInput.defaultSort(), List.of());
    }
}
