package microservice.store_service.application.query.handler;

import libs_kernel.mapper.ResultMapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.application.query.GetStoreByCodeQuery;
import microservice.store_service.application.query.GetStoresByStateQuery;
import microservice.store_service.application.query.result.StoreQueryResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GetStoreByStateQueryHandler {
    private final StoreRepositoryPort storeRepository;
    private final ResultMapper<StoreQueryResult, Store> storeToStoreQueryResultMapper;
    
    @Transactional(readOnly = true)
    public Page<StoreQueryResult> execute(GetStoresByStateQuery query) {
        Page<Store> storePage = storeRepository.findByState(query.state());
        return storeToStoreQueryResultMapper.toResultPage(storePage);
    }
}