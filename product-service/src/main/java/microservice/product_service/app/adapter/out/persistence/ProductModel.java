package microservice.product_service.app.adapter.out.persistence;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.SQLDelete;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import microservice.product_service.app.core.domain.model.enums.ProductCategory;
import microservice.product_service.app.core.domain.model.enums.ProductStatus;
import microservice.product_service.app.core.domain.model.enums.ProductSubcategory;
import microservice.product_service.app.core.domain.model.enums.ProductType;

@Entity
@Table(name = "products", indexes = {
    @Index(name = "idx_product_sku", columnList = "sku"),
    @Index(name = "idx_product_name", columnList = "name"),
    @Index(name = "idx_product_category", columnList = "category"),
    @Index(name = "idx_product_status", columnList = "status"),
    @Index(name = "idx_product_manufacturer", columnList = "manufacturer"),
    @Index(name = "idx_product_barcode", columnList = "barcode"),
    @Index(name = "idx_product_category_status", columnList = "category,status"),
    @Index(name = "idx_product_requires_prescription", columnList = "requires_prescription"),
    @Index(name = "idx_product_created_at", columnList = "created_at"),
    @Index(name = "idx_product_deleted_at", columnList = "deleted_at")
})
@SQLDelete(sql = "UPDATE products SET deleted_at = now() WHERE id = ?")
public class ProductModel {
  @Id
  @Column(name = "id", nullable = false, updatable = false, length = 36)
  private String id;

  @Column(name = "sku", length = 50)
  private String sku;

  @Column(name = "name", nullable = false, length = 255)
  private String name;

  @Column(name = "description", length = 2000)
  private String description;

  @JdbcTypeCode(Types.ARRAY)
  @Column(name = "images", columnDefinition = "text[]")
  private String[] images;

  @Enumerated(EnumType.STRING)
  @Column(name = "type", nullable = false, length = 50)
  private ProductType type;

  @Enumerated(EnumType.STRING)
  @Column(name = "category", nullable = false, length = 50)
  private ProductCategory category;

  @Enumerated(EnumType.STRING)
  @Column(name = "subcategory", length = 50)
  private ProductSubcategory subcategory;

  @Column(name = "expiration_min_months", columnDefinition = "int default 0")
  private int expirationMinMonths;

  @Column(name = "expiration_max_months", columnDefinition = "int default 0")
  private int expirationMaxMonths;

  @Column(name = "active_ingredient", length = 150)
  private String activeIngredient;

  @Column(name = "manufacturer", length = 100)
  private String manufacturer;

  @Column(name = "price", nullable = false, precision = 10, scale = 2)
  private BigDecimal price;

  @Column(name = "barcode", length = 20)
  private String barcode;

  @Column(name = "requires_prescription", nullable = false)
  private boolean requiresPrescription;

  @Enumerated(EnumType.STRING)
  @Column(name = "status", nullable = false, length = 50)
  private ProductStatus status;

  @ElementCollection(fetch = FetchType.EAGER)
  @CollectionTable(name = "product_contraindications", joinColumns = @JoinColumn(name = "product_id"))
  @Column(name = "contraindication", length = 500)
  private List<String> contraindications = new ArrayList<>();

  @Column(name = "dosage", length = 100)
  private String dosage;

  @Column(name = "administration", length = 50)
  private String administration;

  @Column(name = "created_at", nullable = false)
  private LocalDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  @Column(name = "deleted_at")
  private LocalDateTime deletedAt;

  public ProductModel() {
  }

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public String getSKU() {
    return sku;
  }

  public void setSKU(String sku) {
    this.sku = sku;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public String getActiveIngredient() {
    return activeIngredient;
  }

  public void setActiveIngredient(String activeIngredient) {
    this.activeIngredient = activeIngredient;
  }

  public String getManufacturer() {
    return manufacturer;
  }

  public void setManufacturer(String manufacturer) {
    this.manufacturer = manufacturer;
  }

  public ProductCategory getCategory() {
    return category;
  }

  public void setCategory(ProductCategory category) {
    this.category = category;
  }

  public BigDecimal getPrice() {
    return price;
  }

  public void setPrice(BigDecimal price) {
    this.price = price;
  }

  public String getBarcode() {
    return barcode;
  }

  public void setBarcode(String barcode) {
    this.barcode = barcode;
  }

  // No getters/setters for removed dates

  public boolean isRequiresPrescription() {
    return requiresPrescription;
  }

  public void setRequiresPrescription(boolean requiresPrescription) {
    this.requiresPrescription = requiresPrescription;
  }

  public ProductStatus getStatus() {
    return status;
  }

  public void setStatus(ProductStatus status) {
    this.status = status;
  }

  public List<String> getContraindications() {
    return contraindications;
  }

  public void setContraindications(List<String> contraindications) {
    this.contraindications = contraindications;
  }

  public String getDosage() {
    return dosage;
  }

  public void setDosage(String dosage) {
    this.dosage = dosage;
  }

  public String getAdministration() {
    return administration;
  }

  public void setAdministration(String administration) {
    this.administration = administration;
  }

  public LocalDateTime getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(LocalDateTime createdAt) {
    this.createdAt = createdAt;
  }

  public LocalDateTime getUpdatedAt() {
    return updatedAt;
  }

  public void setUpdatedAt(LocalDateTime updatedAt) {
    this.updatedAt = updatedAt;
  }

  public LocalDateTime getDeletedAt() {
    return deletedAt;
  }

  public void setDeletedAt(LocalDateTime deletedAt) {
    this.deletedAt = deletedAt;
  }

  public void markAsRestored() {
    this.deletedAt = null;
  }

  public List<String> getImages() {
    if (images == null) {
      return new ArrayList<>();
    }
    return Arrays.asList(images);
  }

  public void setImages(List<String> images) {
    this.images = images == null ? null : images.toArray(new String[0]);
  }

  public ProductType getType() {
    return type;
  }

  public void setType(ProductType type) {
    this.type = type;
  }

  public ProductSubcategory getSubcategory() {
    return subcategory;
  }

  public void setSubcategory(ProductSubcategory subcategory) {
    this.subcategory = subcategory;
  }

  public int getExpirationMinMonths() {
    return expirationMinMonths;
  }

  public void setExpirationMinMonths(int expirationMinMonths) {
    this.expirationMinMonths = expirationMinMonths;
  }

  public int getExpirationMaxMonths() {
    return expirationMaxMonths;
  }

  public void setExpirationMaxMonths(int expirationMaxMonths) {
    this.expirationMaxMonths = expirationMaxMonths;
  }

  @PrePersist
  protected void onCreate() {
    createdAt = LocalDateTime.now();
    updatedAt = createdAt;
  }

  @PreUpdate
  protected void onUpdate() {
    updatedAt = LocalDateTime.now();
  }
}
