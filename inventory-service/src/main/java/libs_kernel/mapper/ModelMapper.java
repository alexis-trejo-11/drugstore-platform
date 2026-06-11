package libs_kernel.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface ModelMapper<DOMAIN, MODEL> {
    MODEL fromDomain(DOMAIN domain);
    DOMAIN toDomain(MODEL model);
    List<MODEL> fromDomains(List<DOMAIN> domains);
    List<DOMAIN> toDomains(List<MODEL> models);
    Page<DOMAIN> toDomainPage(Page<MODEL> modelPage);
}
