package microservice.store_service.application.handler.query;

import libs_kernel.mapper.ResultMapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.application.handler.result.StoreQueryResult;
import microservice.store_service.application.query.SearchStoresQuery;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Service
public class SearchStoreQueryHandler {
    private final StoreRepositoryPort storeRepository;
    private final ResultMapper<StoreQueryResult, Store> resultMapper;

    public Page<StoreQueryResult> execute(SearchStoresQuery query) {
        var criteria = query.toCriteria();
        List<Store> store = storeRepository.search(criteria);
        long totalCount = storeRepository.count(criteria);
        Pageable pageable = Pageable.ofSize(criteria.size()).withPage(criteria.page());

        Page<Store> resultPage = new PageImpl<>(store, pageable, totalCount);
        return resultMapper.toResultPage(resultPage);
    }
}
