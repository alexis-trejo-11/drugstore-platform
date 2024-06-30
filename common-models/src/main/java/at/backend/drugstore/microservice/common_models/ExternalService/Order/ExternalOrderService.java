package at.backend.drugstore.microservice.common_models.ExternalService.Order;

import at.backend.drugstore.microservice.common_models.DTO.Order.OrderInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

@Service
public interface ExternalOrderService {
    Result<Void> createOrder(OrderInsertDTO orderInsertDTO);
}
