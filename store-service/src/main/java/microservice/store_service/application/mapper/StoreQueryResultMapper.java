package microservice.store_service.application.mapper;

import libs_kernel.mapper.ResultMapper;
import microservice.store_service.application.query.result.StoreQueryResult;
import microservice.store_service.domain.model.Store;
import org.springframework.data.domain.Page;

import java.util.List;

public class StoreQueryResultMapper implements ResultMapper<StoreQueryResult, Store> {
    @Override
    public StoreQueryResult toResult(Store entity) {
        if (entity == null) return null;
        return StoreQueryResult.builder()
                .id(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .is24Hours(entity.is24Hours())
                .status(entity.getStatus())
                .address(entity.getAddress())
                .contactInfo(entity.getContactInfo())
                .geolocation(entity.getGeolocation())
                .serviceSchedule(entity.getServiceSchedule())
                .createdAt(entity.getTimeStamps() != null ? entity.getTimeStamps().getCreatedAt() : null)
                .updatedAt(entity.getTimeStamps() != null ? entity.getTimeStamps().getUpdatedAt() : null)
                .build();
    }

    @Override
    public List<StoreQueryResult> toResults(List<Store> entities) {
        return entities.stream().map(this::toResult).toList();
    }

    @Override
    public Page<StoreQueryResult> toResultPage(Page<Store> entityPage) {
        return entityPage.map(this::toResult);
    }
}
