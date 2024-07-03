package microservice.product_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.product_service.Model.*;
import microservice.product_service.Repository.*;
import microservice.product_service.Utils.ModelTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final MainCategoryRepository mainCategoryRepository;
    private final CategoryRepository categoryRepository;
    private final SubcategoryRepository subcategoryRepository;
    private final SupplierRepository supplierRepository;
    private final BrandRepository brandRepository;

    @Autowired
    public ProductService(ProductRepository productRepository, MainCategoryRepository mainCategoryRepository, CategoryRepository categoryRepository, SubcategoryRepository subcategoryRepository, SupplierRepository supplierRepository, BrandRepository brandRepository) {
        this.productRepository = productRepository;
        this.mainCategoryRepository = mainCategoryRepository;
        this.categoryRepository = categoryRepository;
        this.subcategoryRepository = subcategoryRepository;
        this.supplierRepository = supplierRepository;
        this.brandRepository = brandRepository;
    }


    @Async
    @Transactional
    public List<ProductDTO> getAllProducts() {
        try {
            List<Product> products = productRepository.findAll();

            return products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public List<ProductDTO> getProductsById(List<Long> productId) {
        try {
            List<Product> products = productRepository.findByIdIn(productId);

            return products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public ProductDTO getProductById(Long productId) {
        try {
            Optional<Product> productOptional = productRepository.findById(productId);
            if(productOptional.isEmpty()) {
                return null;
            }
            Product product = productOptional.get();

            return ModelTransformer.productToDTO(product);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public List<ProductDTO> FindProductsBySupplier(Long supplierId) {
        try {
            List<Product> products = productRepository.findBySupplier_Id(supplierId);

            return products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public List<ProductDTO> findProductsByCategoryId(Long categoryId) {
        try {
            List<Product> products = productRepository.findByCategory_Id(categoryId);

            return products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public List<ProductDTO> findProductsBySubCategory(Long subcategoryId) {
        try {
            List<Product> products = productRepository.findBySubcategory_Id(subcategoryId);

            return products.stream()
                    .map(ModelTransformer::productToDTO)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public void insertProduct(ProductInsertDTO productInsertDTO) {
        try {
            Product product = ModelTransformer.insertDtoToProduct(productInsertDTO);

            handleAndSetRelationship(productInsertDTO, product);

            productRepository.saveAndFlush(product);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public boolean updateProduct(Long productId, ProductInsertDTO productInsertDTO) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                return false;
            }

            Long productFoundedId = optionalProduct.get().getId();
            Product product = ModelTransformer.insertDtoToProduct(productInsertDTO);
            product.setUpdatedAt(LocalDateTime.now());
            product.setId(productFoundedId);

            handleAndSetRelationship(productInsertDTO, product);

            productRepository.saveAndFlush(product);

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    @Async
    @Transactional
    public boolean deleteProduct(Long productId) {
        try {
            Optional<Product> optionalProduct = productRepository.findById(productId);
            if (optionalProduct.isEmpty()) {
                return false;
            }

            productRepository.deleteById(productId);

            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
    }

    private void handleAndSetRelationship(ProductInsertDTO productInsertDTO, Product product) {
        Optional<Category> category = categoryRepository.findById(productInsertDTO.getCategoryId());
        Optional<Brand> brand = brandRepository.findById(productInsertDTO.getBrandId());
        Optional<Subcategory> subcategory = subcategoryRepository.findById(productInsertDTO.getSubcategoryId());
        Optional<Supplier> supplier = supplierRepository.findById(productInsertDTO.getSupplierId());
        Optional<MainCategory> mainCategory = mainCategoryRepository.findById(productInsertDTO.getMainCategoryId());
        setProductRelationships(product, brand, category, subcategory, supplier, mainCategory);
    }

    private void setProductRelationships(Product product, Optional<Brand> brand, Optional<Category> category,
                                         Optional<Subcategory> subcategory, Optional<Supplier> supplier,
                                         Optional<MainCategory> mainCategory) {
        product.setCategory(category.orElse(null));
        product.setBrand(brand.orElse(null));
        product.setSubcategory(subcategory.orElse(null));
        product.setSupplier(supplier.orElse(null));
        product.setMainCategory(mainCategory.orElse(null));
    }
}
