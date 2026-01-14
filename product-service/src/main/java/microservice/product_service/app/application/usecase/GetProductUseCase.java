package microservice.product_service.app.application.usecase;

import jakarta.persistence.EntityNotFoundException;
import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.domain.exception.ProductNotFoundException;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.application.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import microservice.product_service.app.domain.model.Product;

@Service
public class GetProductUseCase  {

  private final ProductRepository productRepository;

  @Autowired
  public GetProductUseCase(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

  public Product getProduct(GetProductByIDQuery query) {
    return productRepository.findById(query.productId())
        .orElseThrow(() -> new ProductNotFoundException(query.productId()));
  }
}
