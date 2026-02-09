package microservice.product_service.app.adapter.out.persistence.mapper;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.stereotype.Component;

import microservice.product_service.app.core.domain.model.Product;
import microservice.product_service.app.core.domain.model.ReconstructProductParams;
import microservice.product_service.app.core.domain.model.valueobjects.ActiveIngredient;
import microservice.product_service.app.core.domain.model.valueobjects.Administration;
import microservice.product_service.app.core.domain.model.valueobjects.Dosage;
import microservice.product_service.app.core.domain.model.valueobjects.ExpirationRange;
import microservice.product_service.app.core.domain.model.valueobjects.Manufacturer;
import microservice.product_service.app.core.domain.model.valueobjects.ProductClassification;
import microservice.product_service.app.core.domain.model.valueobjects.ProductDescription;
import microservice.product_service.app.core.domain.model.valueobjects.ProductID;
import microservice.product_service.app.core.domain.model.valueobjects.ProductImages;
import microservice.product_service.app.core.domain.model.valueobjects.ProductName;
import microservice.product_service.app.core.domain.model.valueobjects.ProductPrice;
import microservice.product_service.app.core.domain.model.valueobjects.ProductTimeStamps;
import microservice.product_service.app.core.domain.model.valueobjects.SKU;
import microservice.product_service.app.adapter.out.persistence.ProductModel;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Mapper responsible for converting between Product domain entities and
 * ProductModel persistence entities. Handles bidirectional transformations
 * while maintaining null safety and domain invariants.
 */
@Component
public class ProductModelMapper {

  /**
   * Converts a Product domain entity to a persistence ProductModel.
   *
   * @param product the domain entity to persist, may be null
   * @return the persistence model, or null if input is null
   */
  public ProductModel fromDomain(Product product) {
    if (product == null) {
      return null;
    }

    ProductModel model = new ProductModel();

    mapBasicAttributes(product, model);
    mapValueObjects(product, model);
    mapClassification(product, model);
    mapExpirationRange(product, model);
    mapTimestamps(product, model);
    mapCollections(product, model);

    return model;
  }

  /**
   * Converts a persistence ProductModel back to a Product domain entity.
   *
   * @param model the persisted entity to reconstruct, may be null
   * @return the reconstructed domain entity, or null if input is null
   */
  public Product toDomain(ProductModel model) {
    if (model == null) {
      return null;
    }

    ReconstructProductParams params = buildReconstructionParams(model);
    return Product.reconstruct(params);
  }

  /**
   * Converts a list of persistence models to a list of domain entities.
   *
   * @param models the list of persistence models, may be null
   * @return list of domain entities, empty list if input is null
   */
  public List<Product> toDomainList(List<ProductModel> models) {
    if (models == null) {
      return List.of();
    }

    return models.stream()
        .map(this::toDomain)
        .collect(Collectors.toList());
  }

  /**
   * Converts a paginated result of persistence models to a paginated domain
   * result.
   *
   * @param page the paginated persistence models
   * @return paginated domain entities, empty page if input is null
   */
  public Page<Product> toDomainPage(Page<ProductModel> page) {
    if (page == null) {
      return Page.empty();
    }

    List<Product> products = toDomainList(page.getContent());
    return new PageImpl<>(products, page.getPageable(), page.getTotalElements());
  }

  // Private mapping methods for fromDomain()

  private void mapBasicAttributes(Product product, ProductModel model) {
    model.setId(extractProductId(product));
    model.setSKU(extractSkuValue(product));
    model.setName(extractNameValue(product));
    model.setBarcode(product.getBarcode());
    model.setRequiresPrescription(product.isRequiresPrescription());
    model.setStatus(product.getStatus());
  }

  private void mapValueObjects(Product product, ProductModel model) {
    model.setDescription(extractDescriptionValue(product));
    model.setActiveIngredient(extractActiveIngredientValue(product));
    model.setManufacturer(extractManufacturerValue(product));
    model.setPrice(extractPriceValue(product));
    model.setDosage(extractDosageValue(product));
    model.setAdministration(extractAdministrationValue(product));
  }

  private void mapClassification(Product product, ProductModel model) {
    ProductClassification classification = product.getClassification();
    if (classification != null) {
      model.setType(classification.getType());
      model.setCategory(classification.getCategory());
      model.setSubcategory(classification.getSubcategory());
    }
  }

  private void mapExpirationRange(Product product, ProductModel model) {
    ExpirationRange expirationRange = product.getExpirationRange();
    if (expirationRange != null) {
      model.setExpirationMinMonths(expirationRange.getMinMonths());
      model.setExpirationMaxMonths(expirationRange.getMaxMonths());
    }
  }

  private void mapTimestamps(Product product, ProductModel model) {
    ProductTimeStamps timestamps = product.getTimeStamps();
    if (timestamps != null) {
      model.setCreatedAt(timestamps.getCreatedAt());
      model.setUpdatedAt(timestamps.getUpdatedAt());
      model.setDeletedAt(timestamps.getDeletedAt());
    }
  }

  private void mapCollections(Product product, ProductModel model) {
    model.setImages(extractImageUrls(product));
    model.setContraindications(extractContraindications(product));
  }

  // Private extraction methods for fromDomain()

  private String extractProductId(Product product) {
    return product.getId() != null ? product.getId().value().toString() : null;
  }

  private String extractSkuValue(Product product) {
    return product.getSku() != null ? product.getSku().getValue() : null;
  }

  private String extractNameValue(Product product) {
    return product.getName() != null ? product.getName().value() : null;
  }

  private String extractDescriptionValue(Product product) {
    return product.getDescription() != null ? product.getDescription().getValue() : null;
  }

  private List<String> extractImageUrls(Product product) {
    return product.getImages() != null ? product.getImages().getUrls() : List.of();
  }

  private String extractActiveIngredientValue(Product product) {
    return product.getActiveIngredient() != null ? product.getActiveIngredient().getValue() : null;
  }

  private String extractManufacturerValue(Product product) {
    return product.getManufacturer() != null ? product.getManufacturer().getValue() : null;
  }

  private BigDecimal extractPriceValue(Product product) {
    return product.getPrice() != null ? product.getPrice().value() : null;
  }

  private String extractDosageValue(Product product) {
    return product.getDosage() != null ? product.getDosage().getValue() : null;
  }

  private String extractAdministrationValue(Product product) {
    return product.getAdministration() != null ? product.getAdministration().getValue() : null;
  }

  private List<String> extractContraindications(Product product) {
    return product.getContraindications() != null
        ? new ArrayList<>(product.getContraindications())
        : new ArrayList<>();
  }

  // Private builder methods for toDomain()

  private ReconstructProductParams buildReconstructionParams(ProductModel model) {
    return ReconstructProductParams.builder()
        .id(extractDomainId(model))
        .sku(buildSkuValueObject(model))
        .barcode(model.getBarcode())
        .name(buildProductName(model))
        .description(buildProductDescription(model))
        .manufacturer(buildManufacturer(model))
        .images(buildProductImages(model))
        .classification(buildProductClassification(model))
        .price(buildProductPrice(model))
        .expirationRange(buildExpirationRange(model))
        .activeIngredient(buildActiveIngredient(model))
        .requiresPrescription(model.isRequiresPrescription())
        .status(model.getStatus())
        .contraindications(extractContraindicationsFromModel(model))
        .dosage(buildDosage(model))
        .administration(buildAdministration(model))
        .timeStamps(buildTimeStamps(model))
        .build();
  }

  private ProductID extractDomainId(ProductModel model) {
    return ProductID.from(model.getId());
  }

  private SKU buildSkuValueObject(ProductModel model) {
    return model.getSKU() != null ? new SKU(model.getSKU()) : SKU.NONE;
  }

  private ProductName buildProductName(ProductModel model) {
    return model.getName() != null ? ProductName.create(model.getName()) : ProductName.NONE;
  }

  private ProductDescription buildProductDescription(ProductModel model) {
    return ProductDescription.create(model.getDescription());
  }

  private Manufacturer buildManufacturer(ProductModel model) {
    return Manufacturer.create(model.getManufacturer());
  }

  private ProductImages buildProductImages(ProductModel model) {
    return model.getImages() != null ? new ProductImages(model.getImages()) : ProductImages.EMPTY;
  }

  private ProductClassification buildProductClassification(ProductModel model) {
    return new ProductClassification(
        model.getType(),
        model.getCategory(),
        model.getSubcategory());
  }

  private ProductPrice buildProductPrice(ProductModel model) {
    return model.getPrice() != null ? new ProductPrice(model.getPrice()) : null;
  }

  private ExpirationRange buildExpirationRange(ProductModel model) {
    return new ExpirationRange(model.getExpirationMinMonths(), model.getExpirationMaxMonths());
  }

  private ActiveIngredient buildActiveIngredient(ProductModel model) {
    return model.getActiveIngredient() != null
        ? new ActiveIngredient(model.getActiveIngredient())
        : null;
  }

  private List<String> extractContraindicationsFromModel(ProductModel model) {
    return model.getContraindications() != null
        ? model.getContraindications()
        : new ArrayList<>();
  }

  private Dosage buildDosage(ProductModel model) {
    return model.getDosage() != null ? new Dosage(model.getDosage()) : null;
  }

  private Administration buildAdministration(ProductModel model) {
    return model.getAdministration() != null ? new Administration(model.getAdministration()) : null;
  }

  private ProductTimeStamps buildTimeStamps(ProductModel model) {
    return new ProductTimeStamps(
        model.getCreatedAt(),
        model.getUpdatedAt(),
        model.getDeletedAt());
  }
}
