package io.github.alexisTrejo11.drugstore.inventories.order.sales.infrastructure.adapter.inbound.rest.controller;

import libs_kernel.mapper.ResponseMapper;
import libs_kernel.page.PageResponse;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.core.domain.entity.SaleOrder;
import io.github.alexisTrejo11.drugstore.inventories.order.sales.infrastructure.adapter.inbound.rest.dto.SalesOrderResponse;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class SaleOrderMapper implements ResponseMapper<SalesOrderResponse, SaleOrder> {
    @Override
    public SalesOrderResponse toResponse(SaleOrder saleOrder) {
        return null;
    }

    @Override
    public List<SalesOrderResponse> toResponses(List<SaleOrder> saleOrders) {
        return List.of();
    }

    @Override
    public PageResponse<SalesOrderResponse> toResponsePage(Page<SaleOrder> saleOrders) {
        return null;
    }
}
