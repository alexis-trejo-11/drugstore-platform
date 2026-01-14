package microservice.product_service.app.application;

import jakarta.persistence.EntityNotFoundException;
import microservice.product_service.app.domain.model.valueobjects.ProductID;
import microservice.product_service.app.application.port.in.usecase.GetProductUseCase;
import microservice.product_service.app.application.port.out.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import microservice.product_service.app.domain.model.Product;

@Service
public class GetProductService implements GetProductUseCase {

  private final ProductRepository productRepository;

  @Autowired
  public GetProductService(ProductRepository productRepository) {
    this.productRepository = productRepository;
  }

@Override
  public Product getProduct(ProductID productId) {
        return productRepository.findByID(productId)
