package microservice.ecommerce_payment_service.Config;

import org.jasypt.util.text.AES256TextEncryptor;

public class EncryptionConfig {

    private static final String ENCRYPTION_KEY = "your-secret-key"; // Use a secure key

    private static AES256TextEncryptor textEncryptor;

    static {
        textEncryptor = new AES256TextEncryptor();
        textEncryptor.setPassword(ENCRYPTION_KEY);
    }

    public static String encrypt(String data) {
        return textEncryptor.encrypt(data);
    }

    public static String decrypt(String encryptedData) {
        return textEncryptor.decrypt(encryptedData);
    }
}

