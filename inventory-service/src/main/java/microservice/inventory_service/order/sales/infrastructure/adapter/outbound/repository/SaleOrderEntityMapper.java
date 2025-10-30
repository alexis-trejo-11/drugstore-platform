package microservice.inventory_service.order.sales.infrastructure.adapter.outbound.repository;

import libs_kernel.mapper.EntityMapper;
import microservice.inventory_service.order.sales.core.domain.entity.SaleOrder;
import microservice.inventory_service.order.sales.infrastructure.adapter.outbound.models.SaleOrderEntity;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleOrderEntityMapper implements EntityMapper<SaleOrderEntity, SaleOrder> {
    @Override
    public SaleOrderEntity fromDomain(SaleOrder saleOrder) {
        return null;
    }

    @Override
    public SaleOrder toDomain(SaleOrderEntity model) {
        return null;
    }

    @Override
    public List<SaleOrderEntity> fromDomains(List<SaleOrder> saleOrders) {
        return List.of();
    }

    @Override
    public List<SaleOrder> toDomains(List<SaleOrderEntity> saleOrderEntities) {
        return List.of();
    }

    @Override
    public Page<SaleOrder> toDomainPage(Page<SaleOrderEntity> modelPage) {
        return null;
    }
}
