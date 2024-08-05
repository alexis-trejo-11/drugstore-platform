package at.backend.drugstore.microservice.common_models.ExternalService.Cart;

import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ExternalCartService {

    CompletableFuture<Void> createClientCart(Long clientId);
}
