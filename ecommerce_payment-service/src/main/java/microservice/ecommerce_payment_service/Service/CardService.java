package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;

import java.util.List;

public interface CardService {
    void addCardToClient(CardInsertDTO cardInsertDTO);
    List<CardDTO> getCardByClientId(Long clientId);
    CardDTO getCardById(Long cardId);
    boolean deleteCardById(Long cardId);
}
