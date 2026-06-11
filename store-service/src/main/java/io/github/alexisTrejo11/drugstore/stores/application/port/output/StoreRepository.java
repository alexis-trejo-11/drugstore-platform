package io.github.alexisTrejo11.drugstore.stores.application.port.output;

import io.github.alexisTrejo11.drugstore.stores.domain.model.Store;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreCode;
import io.github.alexisTrejo11.drugstore.stores.domain.model.valueobjects.StoreID;
import io.github.alexisTrejo11.drugstore.stores.domain.specification.StoreSearchCriteria;
import org.springframework.data.domain.Page;

import java.util.Optional;

public interface StoreRepository {
  Store save(Store store);
  Optional<Store> findByID(StoreID id);
  Optional<Store> findByCode(StoreCode code);
  Page<Store> search(StoreSearchCriteria criteria);
  long count(StoreSearchCriteria criteria);
  boolean existsByCode(StoreCode code);
  boolean existsByID(StoreID id);
  void deleteByID(StoreID id);
  void restoreByID(StoreID id);
}
