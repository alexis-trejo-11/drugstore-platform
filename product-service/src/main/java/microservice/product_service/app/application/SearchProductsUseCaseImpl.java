package microservice.product_service.app.application;

import lombok.RequiredArgsConstructor;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.port.in.query.SearchProductsQuery;
import microservice.product_service.app.domain.port.in.usecase.SearchProductsUseCase;
import microservice.product_service.app.domain.port.out.ProductRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class SearchProductsUseCaseImpl implements SearchProductsUseCase {
    private final ProductRepository repository;

    @Override
    public List<Product> searchProducts(SearchProductsQuery query) {
        return repository.findAll();
    }
}
