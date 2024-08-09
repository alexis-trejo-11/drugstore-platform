package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Products;

import at.backend.drugstore.microservice.common_models.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface ProductFacadeService {
    CompletableFuture<List<ProductDTO>> getProductsByIds(List<Long> productIds);
    CompletableFuture<Result<ProductDTO>> getProductById(Long productId);
}