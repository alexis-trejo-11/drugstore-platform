package microservice.store_service.application.query.handler;

import libs_kernel.mapper.ResultMapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.application.query.GetStoreByCodeQuery;
import microservice.store_service.application.query.result.StoreQueryResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetStoreByCodeQueryHandler {
    private final StoreRepositoryPort storeRepository;
    private final ResultMapper<StoreQueryResult, Store> storeToStoreQueryResultMapper;
    
    @Transactional(readOnly = true)
    public StoreQueryResult execute(GetStoreByCodeQuery query) {
        Store store = storeRepository.findByCode(query.code())
                .orElseThrow(() -> new StoreNotFoundException("code", query.code()));

        return storeToStoreQueryResultMapper.toResult(store);
    }
}
