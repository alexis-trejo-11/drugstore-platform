package microservice.product_service.app.application.usecase;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import microservice.product_service.app.application.port.in.command.CreateProductCommand;
import microservice.product_service.app.application.port.in.command.UpdateProductCommand;
import microservice.product_service.app.application.port.in.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.in.query.SearchProductsQuery;
import microservice.product_service.app.application.port.in.usecase.ProductCommandUseCases;
import microservice.product_service.app.application.port.in.usecase.ProductQueryUseCases;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductID;

@Service
public class JoinedProductUseCases implements ProductCommandUseCases, ProductQueryUseCases {
  private final CreateProductUseCase createProductUseCase;
  private final GetProductUseCase getProductUseCase;
  private final SearchProductsUseCase searchProductsUseCase;
  private final UpdateProductUseCase updateProductUseCase;
  private final DeleteProductUseCase deleteProductUseCase;

  @Autowired
  public JoinedProductUseCases(
      CreateProductUseCase createProductUseCase,
      GetProductUseCase getProductUseCase,
      SearchProductsUseCase searchProductsUseCase,
      UpdateProductUseCase updateProductUseCase,
      DeleteProductUseCase deleteProductUseCase) {
    this.createProductUseCase = createProductUseCase;
    this.getProductUseCase = getProductUseCase;
    this.searchProductsUseCase = searchProductsUseCase;
    this.updateProductUseCase = updateProductUseCase;
    this.deleteProductUseCase = deleteProductUseCase;
  }

  @Override
  public Product createProduct(CreateProductCommand command) {
    return createProductUseCase.createProduct(command);
  }

  @Override
  public Product getProductByID(GetProductByIDQuery query) {
    return getProductUseCase.getProduct(query);
  }

  @Override
  public Product updateProduct(UpdateProductCommand command) {
    return updateProductUseCase.updateProduct(command);
  }

  @Override
  public void deleteProduct(ProductID productId) {
    deleteProductUseCase.deleteProduct(productId);
  }

  @Override
  public Page<Product> searchProducts(SearchProductsQuery query) {
    return searchProductsUseCase.searchProducts(query);
  }
}
