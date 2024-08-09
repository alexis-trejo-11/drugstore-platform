package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Payment.CardDTO;
import microservice.ecommerce_payment_service.Config.EncryptionConfig;
import microservice.ecommerce_payment_service.Model.Card;
import org.springframework.stereotype.Service;

@Service
public class CardDomainService {


    public void decryptAndCensureSensitiveData(CardDTO cardDTO) {
        String cardNumber = EncryptionConfig.decrypt(cardDTO.getCardNumber());
        String lastNumbers = cardNumber.substring(cardNumber.length() - 4);
        String cardNumberCensured = "**** **** **** " + lastNumbers;

        cardDTO.setCardNumber(cardNumberCensured);
        cardDTO.setCvv("***");
    }


    public void encryptSensitiveData(Card card) {
        card.setCardNumber(EncryptionConfig.encrypt(card.getCardNumber()));
        card.setCvv(EncryptionConfig.encrypt(card.getCvv()));
    }
}
