package microservice.product_service.app.application.usecase;

import microservice.product_service.app.application.port.input.query.GetProductByBarCodeQuery;
import microservice.product_service.app.application.port.input.query.GetProductBySKUQuery;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import microservice.product_service.app.application.port.input.command.CreateProductCommand;
import microservice.product_service.app.application.port.input.command.UpdateProductCommand;
import microservice.product_service.app.application.port.input.query.GetProductByIDQuery;
import microservice.product_service.app.application.port.input.query.SearchProductsQuery;
import microservice.product_service.app.application.port.input.usecase.ProductCommandUseCases;
import microservice.product_service.app.application.port.input.usecase.ProductQueryUseCases;
import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.valueobjects.ProductID;

@Service
public class JoinedProductUseCases implements ProductCommandUseCases, ProductQueryUseCases {
  private final CreateProductUseCase createProductUseCase;
  private final GetProductUseCases getProductUseCases;
  private final SearchProductsUseCase searchProductsUseCase;
  private final UpdateProductUseCase updateProductUseCase;
  private final DeleteProductUseCase deleteProductUseCase;
  private final RestoreProductUseCase restoreProductUseCase;

  @Autowired
  public JoinedProductUseCases(
      CreateProductUseCase createProductUseCase,
      GetProductUseCases getProductUseCases,
      SearchProductsUseCase searchProductsUseCase,
      UpdateProductUseCase updateProductUseCase,
      DeleteProductUseCase deleteProductUseCase,
      RestoreProductUseCase restoreProductUseCase
      ) {
    this.createProductUseCase = createProductUseCase;
    this.getProductUseCases = getProductUseCases;
    this.searchProductsUseCase = searchProductsUseCase;
    this.updateProductUseCase = updateProductUseCase;
    this.deleteProductUseCase = deleteProductUseCase;
    this.restoreProductUseCase = restoreProductUseCase;
  }

  @Override
  public Product createProduct(CreateProductCommand command) {
    return createProductUseCase.createProduct(command);
  }

  @Override
  public Product getProductByID(GetProductByIDQuery query) {
    return getProductUseCases.getProduct(query);
  }

  @Override
  public Product getProductBySKU(GetProductBySKUQuery query) {
    return getProductUseCases.getProduct(query);
  }

  @Override
  public Product getProductByBarcode(GetProductByBarCodeQuery query) {
    return getProductUseCases.getProduct(query);
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
  public void restoreProduct(ProductID productId){
    restoreProductUseCase.restoreByID(productId);
  }

  @Override
  public Page<Product> searchProducts(SearchProductsQuery query) {
    return searchProductsUseCase.searchProducts(query);
  }
}
