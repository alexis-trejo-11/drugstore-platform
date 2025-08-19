package microservice.auth.app.core.ports.input;

public interface UserServiceClient {
    boolean isEmailUnique(String email);
    boolean isPhoneUnique(String phone);
    void validateUserCredentials(String email, String password);
}
