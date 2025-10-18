package microservice.store_service.application.handler.query;

import libs_kernel.mapper.ResultMapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.application.query.GetStoresByStatusQuery;
import microservice.store_service.application.handler.result.StoreQueryResult;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import microservice.store_service.domain.specification.StoreSearchCriteria;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetStoreByStatusQueryHandler {
    private final StoreRepositoryPort storeRepository;
    private final ResultMapper<StoreQueryResult, Store> storeToStoreQueryResultMapper;
    
    @Transactional(readOnly = true)
    public Page<StoreQueryResult> execute(GetStoresByStatusQuery query) {
        StoreSearchCriteria searchCriteria = StoreSearchCriteria.findByStatus(
                query.status(),
                query.page(),
                query.size(),
                query.sortCriteria()
        );

        List<Store> storePage = storeRepository.search(searchCriteria);
        long totalCount = storeRepository.count(searchCriteria);

        Page<Store> storePageWrapper = new PageImpl<>(
                storePage,
                Pageable.ofSize(query.size()).withPage(query.page()),
                totalCount
        );
        return storeToStoreQueryResultMapper.toResultPage(storePageWrapper);
    }
}