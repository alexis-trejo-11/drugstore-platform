package microservice.product_service.app.application;

import lombok.RequiredArgsConstructor;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.port.in.usecase.DeleteProductUseCase;
import microservice.product_service.app.domain.port.out.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class DeleteUseCaseImpl  implements DeleteProductUseCase {
    private final ProductRepository repository;

    @Override
    public void deleteProduct(ProductId productId) {
        boolean exists = repository.existsById(productId);
        if (!exists) {
            throw new RuntimeException();
        }

        repository.deleteById(productId);
    }
}
