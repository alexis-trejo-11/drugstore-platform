package microservice.product_service.app.application;

import lombok.RequiredArgsConstructor;
import microservice.product_service.app.application.mapper.ProductMapper;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.port.in.command.CreateProductCommand;
import microservice.product_service.app.domain.port.in.usecase.CreateProductUseCase;
import microservice.product_service.app.domain.port.out.ProductRepository;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CreateProductUseCaseImpl implements CreateProductUseCase {
    private final ProductRepository repository;
    private final ProductMapper mapper;

    @Override
    public Product createProduct(CreateProductCommand command) {
        Product newProduct = mapper.createCommandToProduct(command);
        newProduct.validateProduct();
        Product productCreated = repository.save(newProduct);

        return productCreated;
    }
}
