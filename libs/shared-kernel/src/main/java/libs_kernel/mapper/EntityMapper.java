package libs_kernel.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface EntityMapper<Entity, DOMAIN> {
    Entity fromDomains(DOMAIN domain);
    DOMAIN toDomain(Entity model);
    List<Entity> fromDomains(List<DOMAIN> domains);
    List<DOMAIN> toDomain(List<Entity> entities);
    Page<DOMAIN> toDomainPage(Page<Entity> modelPage);
}

