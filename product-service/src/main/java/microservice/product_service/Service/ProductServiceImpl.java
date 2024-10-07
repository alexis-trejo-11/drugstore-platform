package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductRelationsIDs;
import at.backend.drugstore.microservice.common_classes.DTOs.Product.ProductUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.EntityMapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import microservice.product_service.Mappers.ProductMapper;
import microservice.product_service.Model.*;
import microservice.product_service.Service.DomainServices.ProductDomainService;
import org.springframework.cache.annotation.Cacheable;
import microservice.product_service.Repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class ProductServiceImpl implements ProductService {

    private final ProductMapper productMapper;
    private final ProductDomainService productDomainService;
    private final ProductRepository productRepository;

    @Autowired
    public ProductServiceImpl(ProductMapper productMapper, ProductDomainService productDomainService,
                              ProductRepository productRepository) {

        this.productMapper = productMapper;
        this.productDomainService = productDomainService;
        this.productRepository = productRepository;
    }

    @Cacheable(value = "productsById", key = "#productIds")
    public List<ProductDTO> getProductsById(List<Long> productIds) {

        List<Product> products = productRepository.findByIdIn(productIds);
        return products.stream()
                .map(productMapper::productToDTO)
                .collect(Collectors.toList());
    }

    @Override
    @Cacheable(value = "productById", key = "#productId")
    public ProductDTO getProductById(Long productId) {
        Optional<Product> optionalProduct = productRepository.findById(productId);
        return optionalProduct.map(productMapper::productToDTO).orElse(null);
    }

    @Transactional
    @Cacheable(value = "allProductsSortedByCategory", key = "'page-' + #pageable.pageNumber + '-size-' + #pageable.pageSize")
    public Page<ProductDTO> getAllProductsSortedByCategoryHierarchy(Pageable pageable) {
        Page<Product> products = productRepository.findAllSortedByCategoryHierarchy(pageable);
        return products.map(productMapper::productToDTO);
    }

    @Transactional
    @Cacheable(value = "productsBySupplier", key = "#supplierId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getProductsBySupplier(Long supplierId, Pageable pageable) {
            Page<Product> products = productRepository.findBySupplier_Id(supplierId, pageable);
            return products.map(productMapper::productToDTO);
    }

    @Transactional
    @Cacheable(value = "productsByCategory", key = "#categoryId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getProductsByCategoryId(Long categoryId, Pageable pageable) {
            Page<Product> products = productRepository.findByCategory_Id(categoryId, pageable);
            return products.map(productMapper::productToDTO);
    }

    @Transactional
    @Cacheable(value = "productsBySubcategory", key = "#subcategoryId + '-' + #pageable.pageNumber + '-' + #pageable.pageSize")
    public Page<ProductDTO> getProductsBySubCategory(Long subcategoryId, Pageable pageable) {
        Page<Product> products = productRepository.findBySubcategory_Id(subcategoryId, pageable);
        return products.map(productMapper::productToDTO);
    }

    @Transactional
    public Result<Void> createProduct(ProductInsertDTO productInsertDTO) {
        Product product = productMapper.insertDtoToProduct(productInsertDTO);

        // Validate And Set Relationship Values In Model Created
        Result<Void> relationshipResult = productDomainService.handleRelationShips(productInsertDTO.getRelationIDs(), product);
        if (!relationshipResult.isSuccess()) {
         return Result.error(relationshipResult.getErrorMessage());
        }

        productRepository.saveAndFlush(product);
        return Result.success();
    }

    @Transactional
    public Result<Void> updateProduct(ProductUpdateDTO productUpdateDTO) {
        Optional<Product> optionalProduct = productRepository.findById(productUpdateDTO.getId());
        Product product = optionalProduct.get();

        // Map DTO and assign not null values to product
        EntityMapper.mapNonNullProperties(productUpdateDTO, product);

        Result<Void> relationshipResult = productDomainService.handleRelationShips(productUpdateDTO.getRelationIDs(), product);
            if (!relationshipResult.isSuccess()) {
                return Result.error(relationshipResult.getErrorMessage());
            }
            productRepository.saveAndFlush(product);

            return Result.success();
    }

    @Transactional
    public void deleteProduct(Long productId) {
            productRepository.deleteById(productId);
    }
}
