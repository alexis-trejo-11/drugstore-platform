package microservice.auth.app.application.result;

import microservice.auth.app.core.valueobjects.UserId;

public class SignUpResult {
    private UserId userId;
    private String message;

    public static SignUpResult success(UserId userId) {
        SignUpResult result = new SignUpResult();
        result.userId = userId;
        result.message = "User registered successfully. An Email has been sent to the user with the activation link.";
        return  result;
    }
}
