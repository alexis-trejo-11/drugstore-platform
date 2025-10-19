package microservice.store_service.infrastructure.adapter.outbound.persistence.repository;

import libs_kernel.mapper.ModelMapper;
import lombok.RequiredArgsConstructor;
import microservice.store_service.domain.model.Store;
import microservice.store_service.domain.model.valueobjects.StoreCode;
import microservice.store_service.domain.model.valueobjects.StoreID;
import microservice.store_service.domain.port.StoreRepositoryPort;
import microservice.store_service.domain.specification.StoreSearchCriteria;
import microservice.store_service.infrastructure.adapter.outbound.persistence.entity.StoreEntity;
import microservice.store_service.infrastructure.adapter.outbound.persistence.specification.StoreSpecificationBuilder;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
@RequiredArgsConstructor
public class StoreRepositoryImpl implements StoreRepositoryPort {
    private final JpaStoreRepository jpaRepository;
    private final ModelMapper<Store, StoreEntity> mapper;
    private final StoreSpecificationBuilder specificationBuilder;

    @Override
    public Store save(Store store) {
        StoreEntity entity = mapper.fromDomain(store);
        StoreEntity saved = jpaRepository.save(entity);
        return mapper.toDomain(saved);
    }

    @Override
    public Optional<Store> findByID(StoreID id) {
        return jpaRepository.findById(id.value())
                .map(mapper::toDomain);
    }

    @Override
    public Optional<Store> findByCode(StoreCode code) {
        return jpaRepository.findByCode(code.value())
                .map(mapper::toDomain);
    }

    @Override
    public List<Store> search(StoreSearchCriteria criteria) {
        Specification<StoreEntity> spec = specificationBuilder.build(criteria);

        if (criteria.page() != null && criteria.size() != null) {
            // With pagination
            PageRequest pageRequest = buildPageRequest(criteria);
            Page<StoreEntity> page = jpaRepository.findAll(spec, pageRequest);
            return page.getContent().stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        } else {
            // Without pagination
            Sort sort = buildSort(criteria);
            List<StoreEntity> entities = sort.isSorted()
                    ? jpaRepository.findAll(spec, sort)
                    : jpaRepository.findAll(spec);

            return entities.stream()
                    .map(mapper::toDomain)
                    .collect(Collectors.toList());
        }
    }

    @Override
    public long count(StoreSearchCriteria criteria) {
        Specification<StoreEntity> spec = specificationBuilder.build(criteria);
        return jpaRepository.count(spec);
    }

    @Override
    public boolean existsByCode(StoreCode code) {
        return jpaRepository.existsByCode(code.value());
    }

    @Override
    public boolean existsByID(StoreID id) {
        return jpaRepository.existsById(id.value());
    }

    @Override
    public void deleteByID(StoreID id) {
        jpaRepository.deleteById(id.value());
    }

    private PageRequest buildPageRequest(StoreSearchCriteria criteria) {
        Sort sort = buildSort(criteria);
        return PageRequest.of(
                criteria.page(),
                criteria.size(),
                sort
        );
    }

    private Sort buildSort(StoreSearchCriteria criteria) {
        if (criteria.sortBy() == null) {
            return Sort.unsorted();
        }

        String field = criteria.sortBy().field();
        String direction = criteria.sortBy().direction();

        return "ASC".equals(direction)
                ? Sort.by(field).ascending()
                : Sort.by(field).descending();
    }

}