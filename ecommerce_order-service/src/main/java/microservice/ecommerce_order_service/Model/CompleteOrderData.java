package microservice.ecommerce_order_service.Model;

import at.backend.drugstore.microservice.common_models.DTOs.Client.Adress.AddressDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Order.OrderDTO;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CompleteOrderData {
    private OrderDTO orderDTO;
    private AddressDTO addressDTO;
    private ClientDTO clientDTO;
}
