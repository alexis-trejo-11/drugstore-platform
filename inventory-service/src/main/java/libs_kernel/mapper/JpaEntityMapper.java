package libs_kernel.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface JpaEntityMapper<Entity, DOMAIN> {
    Entity fromDomain(DOMAIN domain);
    DOMAIN toDomain(Entity model);
    List<Entity> fromDomains(List<DOMAIN> domains);
    List<DOMAIN> toDomains(List<Entity> entities);
    Page<DOMAIN> toDomainPage(Page<Entity> modelPage);
}

