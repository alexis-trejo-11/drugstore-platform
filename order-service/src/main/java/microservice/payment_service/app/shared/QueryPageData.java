package microservice.payment_service.app.shared;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Sort;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class QueryPageData {
    private int pageNumber;
    private int pageSize;
    private String sortBy;
    private Sort.Direction sortDirection;
}
