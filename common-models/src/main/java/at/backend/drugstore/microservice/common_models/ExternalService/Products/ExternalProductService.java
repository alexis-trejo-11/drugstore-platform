package at.backend.drugstore.microservice.common_models.ExternalService.Products;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ExternalProductService {
    CompletableFuture<List<ProductDTO>> getProductsByIds(List<Long> productIds);
    CompletableFuture<Result<ProductDTO>> getProductById(Long productId);
}