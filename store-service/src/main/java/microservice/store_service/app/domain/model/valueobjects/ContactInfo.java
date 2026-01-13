package microservice.store_service.app.domain.model.valueobjects;

public record ContactInfo(String phone, String email) {
	public static ContactInfo of(String phone, String email) {
		return new ContactInfo(phone, email);
	}

	public static ContactInfo create(String phone, String email) {
		if (phone == null || phone.isBlank()) {
			throw new IllegalArgumentException("Phone number cannot be null or blank.");
		}

		if (phone.length() < 7 || phone.length() > 15) {
			throw new IllegalArgumentException("Phone number must be between 7 and 15 characters long.");
		}

		if (email == null || email.isBlank() || !email.contains("@")) {
			throw new IllegalArgumentException("Email must be a valid email address.");
		}

		return new ContactInfo(phone, email);
	}

	public static ContactInfo NONE = new ContactInfo("", "");
}