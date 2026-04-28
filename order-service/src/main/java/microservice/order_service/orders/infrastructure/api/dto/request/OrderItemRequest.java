package microservice.order_service.orders.infrastructure.api.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

import java.math.BigDecimal;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record OrderItemRequest(
        @NotNull @NotBlank
        String productID,

        @NotNull @NotBlank @Length(min = 3, max = 100)
        String productName,

        @PositiveOrZero @NotNull
        BigDecimal subtotal,

        @PositiveOrZero
        int quantity,

        @NotNull
        Boolean isPrescriptionRequired
) {}
