package microservice.product_service.Utils;

import at.backend.drugstore.microservice.common_models.DTO.Product.ProductInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Product.ProductDTO;
import microservice.product_service.Model.*;

import java.time.LocalDateTime;

import static java.lang.String.valueOf;

public class ModelTransformer {

    public static ProductDTO productToDTO(Product product) {
        ProductDTO productDTO = new ProductDTO();
        productDTO.setId(product.getId());
        productDTO.setName(product.getName());
        productDTO.setImage(product.getImage());
        productDTO.setPrice(product.getPrice());
        productDTO.setProductType(valueOf(product.getProductType()));
        productDTO.setUpc(product.getUpc());
        productDTO.setProductPresentation(valueOf(product.getProductPresentation()));
        productDTO.setContent(product.getContent());
        productDTO.setPackageDimension(product.getPackageDimension());
        productDTO.setPackageDimension(product.getPackageDimension());
        productDTO.setRouteOfAdministration(valueOf(product.getRouteOfAdministration()));
        productDTO.setAgeUsage(product.getAgeUsage());
        productDTO.setPrescriptionRequired(product.isPrescriptionRequired());
        productDTO.setCategory(product.getCategory().getName());
        productDTO.setMainCategory(product.getMainCategory().getName());
        productDTO.setSubcategory(product.getSubcategory().getName());
        productDTO.setBrand(product.getBrand().getName());
        productDTO.setSupplier(product.getSupplier().getName());

        return productDTO;
    }
    public static Product insertDtoToProduct (ProductInsertDTO productInsertDTO) {
        Product product = new Product();

        product.setName(productInsertDTO.getName());
        product.setImage(productInsertDTO.getImage());
        product.setPrice(productInsertDTO.getPrice());
        product.setUpc(productInsertDTO.getUpc());
        product.setAgeUsage(productInsertDTO.getAgeUsage());
        product.setContent(productInsertDTO.getContent());
        product.setProductType(Product.ProductType.valueOf(productInsertDTO.getProductType()));
        product.setPackageDimension(productInsertDTO.getPackageDimension());
        product.setPrescriptionRequired(productInsertDTO.isPrescriptionRequired());
        product.setProductPresentation(Product.ProductPresentation.valueOf(productInsertDTO.getProductPresentation()));
        product.setRouteOfAdministration(Product.RouteOfAdministration.valueOf(productInsertDTO.getRouteOfAdministration()));
        product.setPrescriptionRequired(productInsertDTO.isPrescriptionRequired());
        product.setCreatedAt(LocalDateTime.now());
        product.setUpdatedAt(LocalDateTime.now());

        return product;
    }
}
