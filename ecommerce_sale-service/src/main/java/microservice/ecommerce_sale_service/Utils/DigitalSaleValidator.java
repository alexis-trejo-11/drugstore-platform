package microservice.ecommerce_sale_service.Utils;

import at.backend.drugstore.microservice.common_models.DTOs.Sale.DigitalSaleItemInsertDTO;
import org.springframework.stereotype.Component;

@Component
public class DigitalSaleValidator {

    public void validateSaleCreation(DigitalSaleItemInsertDTO dto) {
        // Implement validation logic here
        if (dto == null || dto.getOrderItemDTOS() == null || dto.getOrderItemDTOS().isEmpty()) {
            throw new IllegalArgumentException("Sale must contain at least one item");
        }
        // Add more validation as needed
    }
}
