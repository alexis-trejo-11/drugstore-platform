package libs_kernel.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface DomainMapper<T, D> {
    D toDto(T t);
    T fromDto(D d);
    List<D> toDTOs(List<T> tList);
    List<T> toEntities(List<D> dList);
    Page<T> toEntityPage(Page<D> dtoPage);
}

