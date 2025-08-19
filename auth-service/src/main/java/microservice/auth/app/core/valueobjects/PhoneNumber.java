package microservice.auth.app.core.valueobjects;

public record PhoneNumber(String value) {
    public PhoneNumber {
        if (value == null || !value.matches("\\+?[0-9]+")) {
             throw new IllegalArgumentException("Invalid phone number");
        }
    }
}
