package microservice.inventory_service.stock.application.query;

import libs_kernel.page.Pagination;

public record GetLowStockInventoriesQuery(Pagination pagination) {
}
