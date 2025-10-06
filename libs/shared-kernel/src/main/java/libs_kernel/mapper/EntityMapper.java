package libs_kernel.mapper;

import org.springframework.data.domain.Page;

import java.util.List;

public interface EntityMapper<MODEL, DOMAIN> {
    MODEL toModel(DOMAIN domain);
    DOMAIN fromModel(MODEL model);
    List<DOMAIN> fromModels(List<MODEL> modelList);
    List<MODEL> toModels(List<DOMAIN> domainList);
    Page<DOMAIN> fromModelPage(Page<MODEL> modelPage);
}

