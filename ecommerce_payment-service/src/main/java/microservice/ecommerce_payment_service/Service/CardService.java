package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;

import java.util.List;
import java.util.Optional;

public interface CardService {
    void addCardToClient(CardInsertDTO cardInsertDTO);
    List<CardDTO> getCardByClientId(Long clientId);
    Optional<CardDTO> getCardById(Long cardId);
    boolean deleteCardById(Long cardId);
    boolean validateClient(Long clientId);
    boolean validateCardData(Long cardId, Long clientId);
    }
