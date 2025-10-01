package microservice.order_service.infrastructure.api.controller.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import microservice.order_service.application.queries.request.GetOrdersByCustomerQuery;
import org.hibernate.validator.constraints.Length;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class GetCustomerOrders {
    @Length(min = 3, max = 50, message = "Status must be between 3 and 50 characters")
    private String status;

    @Min(value = 1, message = "Page number must be at least 1")
    private int page = 1;

    @Min(value = 1, message = "Page size must be at least 1")
    @Max(value = 100, message = "Page size must be at most 100")
    private int size = 10;

    public GetOrdersByCustomerQuery toCommand(String customerId) {
        return GetOrdersByCustomerQuery.builder()
            .customerId(customerId)
            .page(this.page)
            .size(this.size)
            .build();
    }
}
