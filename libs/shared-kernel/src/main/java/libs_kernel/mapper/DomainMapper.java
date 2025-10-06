package libs_kernel.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface DomainMapper<ENTITY, DTO> {
    DTO toDto(ENTITY entity);
    ENTITY fromDto(DTO dto);
    List<DTO> toDTOs(List<ENTITY> entityList);
    List<ENTITY> toEntities(List<DTO> dtoList);
    Page<ENTITY> toEntityPage(Page<DTO> dtoPage);
}

