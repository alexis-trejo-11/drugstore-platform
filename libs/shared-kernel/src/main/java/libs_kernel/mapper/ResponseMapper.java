package libs_kernel.mapper;

import libs_kernel.page.PageResponse;
import org.springframework.data.domain.Page;

import java.util.List;

public interface ResponseMapper<R, DTO> {
    R toResponse(DTO dto);
    List<R> toResponses(List<DTO> dtoList);
    PageResponse<R> toResponsePage(Page<DTO> dtoPage);
}
