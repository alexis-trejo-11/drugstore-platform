package microservice.ecommerce_payment_service.Utils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.Base64;

@Converter
public class CreditCardConverter implements AttributeConverter<String, String> {

    private static final String SECRET_KEY = "your-secret-key";

    @Override
    public String convertToDatabaseColumn(String attribute) {
        return Base64.getEncoder().encodeToString(attribute.getBytes());
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        return new String(Base64.getDecoder().decode(dbData));
    }
}
