package microservice.product_service.app.domain.model.enums;

import java.util.Arrays;
import java.util.List;

/**
 * Specific subcategory within a ProductCategory.
 * Hierarchy: ProductType -> ProductCategory -> ProductSubcategory
 *
 * Examples:
 * - MEDICATION/DERMATOLOGY/ANTI_ACNE
 * - MEDICATION/ANALGESICS/ANTI_INFLAMMATORY
 */
public enum ProductSubcategory {
  // Dermatology subcategories
  ANTI_ACNE("Anti-acne", ProductCategory.DERMATOLOGY),
  ANTI_FUNGAL("Anti-fungal", ProductCategory.DERMATOLOGY),
  MOISTURIZERS("Moisturizers", ProductCategory.DERMATOLOGY),
  SUNSCREEN("Sunscreen", ProductCategory.DERMATOLOGY),
  WOUND_CARE("Wound Care", ProductCategory.DERMATOLOGY),

  // Analgesics subcategories
  ANTI_INFLAMMATORY("Anti-inflammatory", ProductCategory.ANALGESICS),
  FEVER_REDUCER("Fever Reducer", ProductCategory.ANALGESICS),
  MUSCLE_RELAXANT("Muscle Relaxant", ProductCategory.ANALGESICS),
  MIGRAINE_RELIEF("Migraine Relief", ProductCategory.ANALGESICS),

  // Antibiotics subcategories
  PENICILLINS("Penicillins", ProductCategory.ANTIBIOTICS),
  CEPHALOSPORINS("Cephalosporins", ProductCategory.ANTIBIOTICS),
  MACROLIDES("Macrolides", ProductCategory.ANTIBIOTICS),
  FLUOROQUINOLONES("Fluoroquinolones", ProductCategory.ANTIBIOTICS),
  TOPICAL_ANTIBIOTICS("Topical Antibiotics", ProductCategory.ANTIBIOTICS),

  // Vitamins subcategories
  MULTIVITAMINS("Multivitamins", ProductCategory.VITAMINS),
  VITAMIN_B_COMPLEX("Vitamin B Complex", ProductCategory.VITAMINS),
  VITAMIN_C("Vitamin C", ProductCategory.VITAMINS),
  VITAMIN_D("Vitamin D", ProductCategory.VITAMINS),
  PRENATAL("Prenatal Vitamins", ProductCategory.VITAMINS),

  // Cardiology subcategories
  ANTIHYPERTENSIVE("Antihypertensive", ProductCategory.CARDIOLOGY),
  ANTICOAGULANT("Anticoagulant", ProductCategory.CARDIOLOGY),
  CHOLESTEROL_CONTROL("Cholesterol Control", ProductCategory.CARDIOLOGY),

  // Respiratory subcategories
  BRONCHODILATOR("Bronchodilator", ProductCategory.RESPIRATORY),
  ANTIHISTAMINE("Antihistamine", ProductCategory.RESPIRATORY),
  DECONGESTANT("Decongestant", ProductCategory.RESPIRATORY),
  COUGH_SUPPRESSANT("Cough Suppressant", ProductCategory.RESPIRATORY),

  // Digestive subcategories
  ANTACID("Antacid", ProductCategory.DIGESTIVE),
  LAXATIVE("Laxative", ProductCategory.DIGESTIVE),
  ANTI_DIARRHEAL("Anti-diarrheal", ProductCategory.DIGESTIVE),
  PROBIOTIC("Probiotic", ProductCategory.DIGESTIVE),

  // Pediatric subcategories
  PEDIATRIC_ANALGESIC("Pediatric Analgesic", ProductCategory.PEDIATRIC),
  PEDIATRIC_ANTIBIOTIC("Pediatric Antibiotic", ProductCategory.PEDIATRIC),
  PEDIATRIC_VITAMIN("Pediatric Vitamin", ProductCategory.PEDIATRIC),

  // Medical Devices subcategories
  MONITORING_DEVICES("Monitoring Devices", ProductCategory.MEDICAL_DEVICES),
  MOBILITY_AIDS("Mobility Aids", ProductCategory.MEDICAL_DEVICES),
  DIAGNOSTIC_EQUIPMENT("Diagnostic Equipment", ProductCategory.MEDICAL_DEVICES),
  FIRST_AID("First Aid", ProductCategory.MEDICAL_DEVICES),

  // Personal Care subcategories
  ORAL_HYGIENE("Oral Hygiene", ProductCategory.PERSONAL_CARE),
  HAIR_CARE("Hair Care", ProductCategory.PERSONAL_CARE),
  SKIN_CARE("Skin Care", ProductCategory.PERSONAL_CARE),
  FEMININE_HYGIENE("Feminine Hygiene", ProductCategory.PERSONAL_CARE),

  // Cosmetics subcategories
  MAKEUP("Makeup", ProductCategory.COSMETICS),
  FRAGRANCES("Fragrances", ProductCategory.COSMETICS),
  NAIL_CARE("Nail Care", ProductCategory.COSMETICS),

  // Generic fallback
  GENERAL("General", ProductCategory.NOT_CATEGORIZED),
  NOT_SPECIFIED("Not Specified", ProductCategory.NOT_CATEGORIZED);

  private final String displayName;
  private final ProductCategory parentCategory;

  ProductSubcategory(String displayName, ProductCategory parentCategory) {
    this.displayName = displayName;
    this.parentCategory = parentCategory;
  }

  public String getDisplayName() {
    return displayName;
  }

  public ProductCategory getParentCategory() {
    return parentCategory;
  }

  public static List<ProductSubcategory> getByCategory(ProductCategory category) {
    return Arrays.stream(ProductSubcategory.values())
        .filter(sub -> sub.parentCategory == category)
        .toList();
  }

  public static List<String> getDisplayNamesByCategory(ProductCategory category) {
    return getByCategory(category).stream()
        .map(ProductSubcategory::getDisplayName)
        .toList();
  }

  public static ProductSubcategory fromDisplayName(String displayName) {
    return Arrays.stream(ProductSubcategory.values())
        .filter(s -> s.displayName.equalsIgnoreCase(displayName))
        .findFirst()
        .orElse(NOT_SPECIFIED);
  }

  public boolean belongsTo(ProductCategory category) {
    return this.parentCategory == category;
  }
}
