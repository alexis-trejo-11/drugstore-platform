package org.github.alexisTrejo11.drugstore.stores.infrastructure.outbound.persistence.repository;

import libs_kernel.mapper.ModelMapper;
import lombok.extern.slf4j.Slf4j;
import org.github.alexisTrejo11.drugstore.stores.domain.model.Store;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import org.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;
import org.github.alexisTrejo11.drugstore.stores.application.port.out.StoreRepository;
import org.github.alexisTrejo11.drugstore.stores.domain.specification.StoreSearchCriteria;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.outbound.persistence.entity.StoreEntity;
import org.github.alexisTrejo11.drugstore.stores.infrastructure.outbound.persistence.specification.StoreSpecificationBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
@Slf4j
public class StoreRepositoryImpl implements StoreRepository {
	private final JpaStoreRepository jpaRepository;
	private final ModelMapper<Store, StoreEntity> mapper;
	private final StoreSpecificationBuilder specificationBuilder;

	@Autowired
	public StoreRepositoryImpl(
			JpaStoreRepository jpaRepository,
			ModelMapper<Store, StoreEntity> mapper,
			StoreSpecificationBuilder specificationBuilder) {
		this.jpaRepository = jpaRepository;
		this.mapper = mapper;
		this.specificationBuilder = specificationBuilder;
	}

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
	public Page<Store> search(StoreSearchCriteria criteria) {
		Specification<StoreEntity> spec = specificationBuilder.build(criteria);
		Page<StoreEntity> page = jpaRepository.findAll(spec, buildPageRequest(criteria));
		return mapper.toDomainPage(page);
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

	@Override
	public void restoreByID(StoreID id) {
		Optional<StoreEntity> optional = jpaRepository.findByIdIncludeDeleted(id.value());
		if (optional.isPresent()) {
			StoreEntity entity = optional.get();
			entity.markAsRestored();

			jpaRepository.save(entity);
		} else {
			log.warn("Attempted to restore non-existing store with id {}", id.value());
		}
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
