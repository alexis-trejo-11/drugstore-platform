package at.backend.drugstore.microservice.common_models.ExternalService.Products;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface ExternalProductService {

    Result<List<ProductDTO>> getProductsByIds(List<Long> productIds);
    Result<ProductDTO> getProductById(Long productId);
}
