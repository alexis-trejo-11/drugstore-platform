package io.github.alexisTrejo11.drugstore.carts.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageMetadata {
  private long totalElements;
  private int totalPages;
  private int currentPage;
  private int pageSize;
  private boolean hasNext;
  private boolean hasPrevious;

  public static PageMetadata fromPage(Page<?> page) {
    PageMetadata pageMetadata = new PageMetadata();
    pageMetadata.totalElements = page.getTotalElements();
    pageMetadata.totalPages = page.getTotalPages();
    pageMetadata.currentPage = page.getNumber();
    pageMetadata.pageSize = page.getSize();
    pageMetadata.hasNext = page.hasNext();
    pageMetadata.hasPrevious = page.hasPrevious();

    return pageMetadata;
  }
}
