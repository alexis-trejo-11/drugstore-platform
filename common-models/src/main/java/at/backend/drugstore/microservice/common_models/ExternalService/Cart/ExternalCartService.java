package at.backend.drugstore.microservice.common_models.ExternalService.Cart;

import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface ExternalCartService {

    Result<Void> createClientCart(Long clientId);
}
