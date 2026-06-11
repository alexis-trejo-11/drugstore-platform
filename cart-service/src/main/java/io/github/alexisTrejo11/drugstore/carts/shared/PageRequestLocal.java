package io.github.alexisTrejo11.drugstore.carts.shared;

import libs_kernel.page.SortInput;
import org.springframework.data.domain.Pageable;


import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PageRequestLocal {
  @Min(1)
  private int page = 1;
  @Min(1)
  @Max(100)
  private int size = 10;
  private SortInput sortInput = SortInput.defaultSort();

  public static PageRequestLocal of(int page, int size, SortInput sortInput) {
    return new PageRequestLocal(page, size, sortInput);
  }

  public static PageRequestLocal of(int page, int size, String sortBy, String direction) {
    return new PageRequestLocal(page, size, new SortInput(sortBy, direction));
  }

  public static PageRequestLocal defaultPageRequest() {
    return new PageRequestLocal(1, 10, SortInput.defaultSort());
  }

  public Pageable toPageable() {
    return Pageable.ofSize(size).withPage(page - 1);
  }
}