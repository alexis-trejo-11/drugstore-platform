package libs_kernel.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ResultMapper<R, I> {
    R toResult(I entity);
    List<R> toResults(List<I> entities);
    Page<R> toResultPage(Page<I> entityPage);
}
