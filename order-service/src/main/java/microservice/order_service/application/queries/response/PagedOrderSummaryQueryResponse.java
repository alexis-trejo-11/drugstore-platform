package microservice.order_service.application.queries.response;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class PagedOrderSummaryQueryResponse {
    private List<OrderSummaryQueryResponse> orders;
    private int currentPage;
    private int totalPages;
    private long totalElements;
    private int size;
    private boolean hasNext;
    private boolean hasPrevious;
}
