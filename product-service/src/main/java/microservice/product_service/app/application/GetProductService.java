package microservice.product_service.app.application;


import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.app.domain.model.ProductId;
import microservice.product_service.app.domain.port.in.usecase.GetProductUseCase;
import microservice.product_service.app.domain.port.out.ProductRepository;
import org.springframework.stereotype.Service;
import microservice.product_service.app.domain.model.Product;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetProductService implements GetProductUseCase {

    private final ProductRepository productRepository;

    @Override
    public Product getProduct(ProductId productId) {
        return productRepository.findById(productId)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Product not found with id: " + productId));
    }
}