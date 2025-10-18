package microservice.store_service.application.handler.query;



import libs_kernel.mapper.ResultMapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.application.query.GetStoreByIDQuery;
import microservice.store_service.application.handler.result.StoreQueryResult;
import microservice.store_service.domain.exception.StoreNotFoundException;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.port.StoreRepositoryPort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class GetStoreByIDQueryHandler {
    private final StoreRepositoryPort storeRepository;
    private final ResultMapper<StoreQueryResult, Store> storeToStoreQueryResultMapper;

    @Transactional(readOnly = true)
    public StoreQueryResult execute(GetStoreByIDQuery query) {
        Store store = storeRepository.findByID(query.id())
                .orElseThrow(() -> new StoreNotFoundException("id", query.id().toString()));

        return storeToStoreQueryResultMapper.toResult(store);
    }
}
