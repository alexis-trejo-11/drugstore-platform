package microservice.order_service.infrastructure.api.controller.dto;

import libs_kernel.page.PaginationMetadata;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PagedOrderResponse {
    private PaginationMetadata metadata;
    private OrderResponse[] orders;
}

