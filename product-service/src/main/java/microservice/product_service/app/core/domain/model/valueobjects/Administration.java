package microservice.product_service.app.core.domain.model.valueobjects;

import microservice.product_service.app.core.domain.exception.ProductValueObjectException;

import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;

public record Administration(String value) {
	public static final Administration ORAL = new Administration("ORAL");
	public static final Administration TOPICAL = new Administration("TOPICAL");
	public static final Administration INTRAVENOUS = new Administration("INTRAVENOUS");
	public static final Administration INTRAMUSCULAR = new Administration("INTRAMUSCULAR");
	public static final Administration SUBCUTANEOUS = new Administration("SUBCUTANEOUS");
	public static final Administration INHALATION = new Administration("INHALATION");
	public static final Administration NOT_SPECIFIED = new Administration("NOT_SPECIFIED");
	public static final Administration NONE = new Administration("");

	private static final Set<String> VALID_ADMINISTRATION_TYPES = Set.of(
			"ORAL", "TOPICAL", "INTRAVENOUS", "INTRAMUSCULAR",
			"SUBCUTANEOUS", "INHALATION", "OPHTHALMIC", "OTIC",
			"NASAL", "RECTAL", "VAGINAL", "TRANSDERMAL"
	);

	public static Administration create(String value) {
		if (value == null || value.trim().isEmpty()) {
			return NONE;
		}

		String trimmed = value.trim().toUpperCase();

		if (trimmed.length() > 100) {
			throw new ProductValueObjectException("Administration", "Administration method cannot exceed 100 characters");
		}

		if (!VALID_ADMINISTRATION_TYPES.contains(trimmed) &&
				!trimmed.equals("NOT_SPECIFIED") &&
				!trimmed.isEmpty()) {

			if (!trimmed.matches("^[A-Z\\s\\-]+$")) {
				throw new ProductValueObjectException("Administration",
						"Administration method contains invalid characters. Only letters, spaces, and hyphens are allowed.");
			}

			if (trimmed.length() < 2) {
				throw new ProductValueObjectException("Administration", "Administration method must be at least 2 characters long");
			}
		}

		return new Administration(trimmed);
	}

	public static Administration fromCommonType(String type) {
		if (type == null || type.trim().isEmpty()) {
			return NONE;
		}

		String normalized = type.trim().toUpperCase();

		return switch (normalized) {
			case "ORAL" -> ORAL;
			case "TOPICAL" -> TOPICAL;
			case "INTRAVENOUS", "IV" -> INTRAVENOUS;
			case "INTRAMUSCULAR", "IM" -> INTRAMUSCULAR;
			case "SUBCUTANEOUS", "SC" -> SUBCUTANEOUS;
			case "INHALATION", "INHALED" -> INHALATION;
			case "NOT_SPECIFIED", "UNSPECIFIED" -> NOT_SPECIFIED;
			default -> create(type);
		};
	}

	public static Set<Administration> getAllPredefined() {
		return Set.of(ORAL, TOPICAL, INTRAVENOUS, INTRAMUSCULAR,
				SUBCUTANEOUS, INHALATION, NOT_SPECIFIED);
	}

	public boolean isEmpty() {
		return value == null || value.isEmpty();
	}

	public boolean isSpecified() {
		return !isEmpty() && !this.equals(NOT_SPECIFIED);
	}

	public boolean isPredefined() {
		return VALID_ADMINISTRATION_TYPES.contains(value) ||
				this.equals(NOT_SPECIFIED) ||
				this.equals(NONE);
	}

	public String getValue() {
		return value != null ? value : "";
	}

	public String getDisplayName() {
		if (isEmpty() || this.equals(NOT_SPECIFIED)) {
			return "Not specified";
		}

		// (ej: "INTRAVENOUS" -> "Intravenous")
		return Arrays.stream(value.split("[\\s\\-]+"))
				.map(word -> word.substring(0, 1).toUpperCase() +
						word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}
}