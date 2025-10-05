package libs_kernel.page;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
public class PageMapper {

    /**
     * Convierte PageInput a Pageable de Spring
     */
    public Pageable toPageable(PageInput pageInput) {
        if (pageInput == null) {
            return PageInput.defaultPageInput().toPageable();
        }
        return pageInput.toPageable();
    }

    /**
     * Convierte parámetros simples a Pageable
     */
    public Pageable toPageable(Integer page, Integer size, String sortBy, String direction) {
        PageInput pageInput = new PageInput(
                page != null ? page : 0,
                size != null ? size : 10,
                new SortInput(sortBy, direction));
        return pageInput.toPageable();
    }

    /**
     * Mapea una Page de Spring a una respuesta personalizada
     */
    public <T, R> PageResponse<R> toPageResponse(Page<T> page, Function<T, R> mapper) {
        List<R> content = page.getContent().stream()
                .map(mapper)
                .collect(Collectors.toList());

        return new PageResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious());
    }

    /**
     * Mapea una Page de Spring sin transformar el contenido
     */
    public <T> PageResponse<T> toPageResponse(Page<T> page) {
        return new PageResponse<>(
                page.getContent(),
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isFirst(),
                page.isLast(),
                page.hasNext(),
                page.hasPrevious());
    }

    /**
     * Crea una PageResponse desde una lista simple
     */
    public <T> PageResponse<T> toPageResponse(List<T> content, PageInput pageInput, long totalElements) {
        int totalPages = (int) Math.ceil((double) totalElements / pageInput.size());

        return new PageResponse<>(
                content,
                pageInput.page(),
                pageInput.size(),
                totalElements,
                totalPages,
                pageInput.page() == 0,
                pageInput.page() >= totalPages - 1,
                pageInput.page() < totalPages - 1,
                pageInput.page() > 0);
    }

    /**
     * Convierte una lista a Page con paginación manual
     */
    public <T> Page<T> toPage(List<T> items, Pageable pageable) {
        int start = (int) pageable.getOffset();
        int end = Math.min((start + pageable.getPageSize()), items.size());

        List<T> pageContent = items.subList(start, end);

        return new PageImpl<>(pageContent, pageable, items.size());
    }
}
