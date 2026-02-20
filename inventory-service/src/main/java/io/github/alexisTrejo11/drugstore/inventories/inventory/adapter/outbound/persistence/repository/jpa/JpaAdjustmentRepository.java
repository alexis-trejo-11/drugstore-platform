package io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.repository.jpa;

import io.github.alexisTrejo11.drugstore.inventories.inventory.adapter.outbound.persistence.model.StockAdjustmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaAdjustmentRepository extends JpaRepository<StockAdjustmentEntity, String> {
}
