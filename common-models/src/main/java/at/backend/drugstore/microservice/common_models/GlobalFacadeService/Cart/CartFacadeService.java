package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Cart;

import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface CartFacadeService {

    CompletableFuture<Void> createClientCart(Long clientId);
}
