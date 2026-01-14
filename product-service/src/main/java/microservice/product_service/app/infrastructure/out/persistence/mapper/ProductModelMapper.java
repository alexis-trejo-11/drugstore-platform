package microservice.product_service.app.infrastructure.out.persistence.mapper;

import microservice.product_service.app.domain.model.Product;
import microservice.product_service.app.domain.model.ReconstructProductParams;
import microservice.product_service.app.domain.model.valueobjects.*;
import microservice.product_service.app.infrastructure.out.persistence.ProductModel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ProductModelMapper {

  public ProductModel fromDomain(Product product) {
    if (product == null) {
      return null;
    }

    ProductModel model = new ProductModel();
    model.setId(product.getId().value().toString());
    model.setCode(product.getCode().value());
    model.setName(product.getName().value());
    model.setDescription(product.getDescription() != null ? product.getDescription().getValue() : null);
    model.setActiveIngredient(product.getActiveIngredient() != null ? product.getActiveIngredient().getValue() : null);
    model.setManufacturer(product.getManufacturer() != null ? product.getManufacturer().getValue() : null);
    model.setCategory(product.getCategory());
    model.setPrice(product.getPrice() != null ? product.getPrice().value() : null);
    model.setBarcode(product.getBarcode());
    model.setExpirationDate(product.getDates() != null ? product.getDates().expirationDate() : null);
    model.setManufactureDate(product.getDates() != null ? product.getDates().manufactureDate() : null);
    model.setRequiresPrescription(product.isRequiresPrescription());
    model.setStatus(product.getStatus());
    model.setContraindications(
        product.getContraindications() != null ? new HashSet<>(product.getContraindications()) : new HashSet<>());
    model.setDosage(product.getDosage() != null ? product.getDosage().getValue() : null);
    model.setAdministration(product.getAdministration() != null ? product.getAdministration().getValue() : null);
    model.setCreatedAt(product.getTimeStamps() != null ? product.getTimeStamps().getCreatedAt() : null);
    model.setUpdatedAt(product.getTimeStamps() != null ? product.getTimeStamps().getUpdatedAt() : null);

    if (product.getTimeStamps() != null && product.getTimeStamps().getDeletedAt() != null) {
      model.setDeletedAt(product.getTimeStamps().getDeletedAt());
    }

    return model;
  }

  public Product toDomain(ProductModel model) {
    if (model == null) {
      return null;
    }

    ReconstructProductParams params = ReconstructProductParams.builder()
        .id(ProductID.from(model.getId()))
        .code(new ProductCode(model.getCode() != null ? model.getCode() : "NONE"))
        .barcode(model.getBarcode())
        .name(new ProductName(model.getName()))
        .description(new ProductDescription(model.getDescription()))
        .activeIngredient(new ActiveIngredient(model.getActiveIngredient()))
        .manufacturer(new Manufacturer(model.getManufacturer()))
        .category(model.getCategory())
        .price(new ProductPrice(model.getPrice()))
        .dates(new ProductDates(model.getManufactureDate(), model.getExpirationDate()))
        .requiresPrescription(model.isRequiresPrescription())
        .status(model.getStatus())
        .contraindications(model.getContraindications() != null ? model.getContraindications() : new HashSet<>())
        .dosage(new Dosage(model.getDosage()))
        .administration(new Administration(model.getAdministration()))
        .timeStamps(new ProductTimeStamps(model.getCreatedAt(), model.getUpdatedAt(), model.getDeletedAt()))
        .build();

    return Product.reconstruct(params);
  }

  public List<Product> toDomainList(List<ProductModel> models) {
    if (models == null) {
      return List.of();
    }
    return models.stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  public Page<Product> toDomainPage(Page<ProductModel> page) {
    if (page == null) {
      return Page.empty();
    }
    List<Product> products = toDomainList(page.getContent());
    return new PageImpl<>(products, page.getPageable(), page.getTotalElements());
  }
}
