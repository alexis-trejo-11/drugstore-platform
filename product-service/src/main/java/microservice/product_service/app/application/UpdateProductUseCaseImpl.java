package microservice.product_service.app.application;

import lombok.RequiredArgsConstructor;
import microservice.product_service.app.application.mapper.ProductMapper;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.port.in.command.UpdateProductCommand;
import microservice.product_service.app.domain.port.in.usecase.UpdateProductUseCase;
import microservice.product_service.app.domain.port.out.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UpdateProductUseCaseImpl implements UpdateProductUseCase {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Product updateProduct(UpdateProductCommand command) {
        Product existingProduct = repository.findById(command.getProductId())
                .orElseThrow(IllegalArgumentException::new);

        mapper.updateProductFromCommand(command, existingProduct);
        Product updatedProduct = repository.save(existingProduct);
        return updatedProduct;
    }
}
