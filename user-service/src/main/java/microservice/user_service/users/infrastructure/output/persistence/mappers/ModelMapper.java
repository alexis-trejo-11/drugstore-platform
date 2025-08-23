package microservice.user_service.users.infrastructure.output.persistence.mappers;

import java.util.List;

import org.springframework.data.domain.Page;

import microservice.user_service.utils.page.PageResponse;

public interface ModelMapper<E, M> {
    M fromEntity(E entity);

    E toEntity(M model);

    List<M> fromEntities(List<E> entities);

    List<E> toEntities(List<M> models);

    PageResponse<E> toPageResponse(Page<M> modelPage);
}