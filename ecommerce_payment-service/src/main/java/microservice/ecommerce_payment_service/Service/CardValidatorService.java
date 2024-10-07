package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.Utils.Result;

public interface CardValidatorService {
    boolean validateClient(Long clientId);
    boolean validateCardData(Long cardId, Long clientId);
    boolean validateExistingCard(Long cardId);
    boolean validateCard(String cardNumber);
    Result<Void> validateNewCard(String requestedCardNumber, Long clientId);
}
