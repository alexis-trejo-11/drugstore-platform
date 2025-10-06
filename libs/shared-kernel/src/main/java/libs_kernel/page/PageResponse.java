package libs_kernel.page;

import java.util.List;

/**
 * Interface that defines the contract for paginated responses.
 * This interface provides read-only access to pagination metadata and content.
 *
 * @param <T> the type of elements in the page content
 */
public interface PageResponse<T> {

    /**
     * Gets the content of the current page.
     *
     * @return a list of elements in the current page
     */
    List<T> content();

    /**
     * Gets the current page number (zero-based).
     *
     * @return the page number
     */
    int page();

    /**
     * Gets the size of the page.
     *
     * @return the number of elements per page
     */
    int size();

    /**
     * Gets the total number of elements across all pages.
     *
     * @return the total number of elements
     */
    long totalElements();

    /**
     * Gets the total number of pages.
     *
     * @return the total number of pages
     */
    int totalPages();

    /**
     * Checks if this is the first page.
     *
     * @return true if this is the first page, false otherwise
     */
    boolean first();

    /**
     * Checks if this is the last page.
     *
     * @return true if this is the last page, false otherwise
     */
    boolean last();

    /**
     * Checks if there is a next page.
     *
     * @return true if there is a next page, false otherwise
     */
    boolean hasNext();

    /**
     * Checks if there is a previous page.
     *
     * @return true if there is a previous page, false otherwise
     */
    boolean hasPrevious();


    /**
     * Populates the PageResponse with data from a Spring Data Page object.
     *
     * @param page the Spring Data Page object
     * @return the populated PageResponse
     */
    PageResponse<T> fromPage(org.springframework.data.domain.Page<T> page);
}

