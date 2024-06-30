package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
import microservice.ecommerce_payment_service.Automappers.CardMapper;
import microservice.ecommerce_payment_service.Config.EncryptionConfig;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Repository.CardRepository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardMapper cardMapper;
    private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);


    @Autowired
    public CardServiceImpl(CardRepository cardRepository, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.cardMapper = cardMapper;
    }

    @Async
    @Transactional
    @Override
    public void addCardToClient(CardInsertDTO cardInsertDTO) {
        try {
            Card card = cardMapper.toEntity(cardInsertDTO);
            encryptSensitiveData(card);
            cardRepository.saveAndFlush(card);
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while adding the card", e);
        }
    }

    @Async
    public CardDTO getCardById(Long cardId) {
        try {
            Optional<Card> optionalCard = cardRepository.findById(cardId);
            if(optionalCard.isEmpty()) {
                return null;
            }
            Card card = optionalCard.get();

            CardDTO cardDTO = cardMapper.toDto(card);
            decryptAndCensureSensitiveData(cardDTO);

            return cardDTO;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while finding card", e);
        }
    }

    @Async
    public List<CardDTO> getCardByClientId(Long clientId) {
        try {
            List<Card> cards = cardRepository.findByClientId(clientId);
            if (cards.isEmpty()) {
                return new ArrayList<>();
            }

            List<CardDTO> cardDTOS = new ArrayList<>();
            for(Card card : cards ) {
                CardDTO cardDTO = cardMapper.toDto(card);
                decryptAndCensureSensitiveData(cardDTO);
                cardDTOS.add(cardDTO);
            }
            return cardDTOS;
        } catch (Exception e) {
            throw new RuntimeException("An error occurred while finding card", e);
        }
    }

    @Async
    @Override
    public boolean deleteCardById(Long cardId) {
        try {
            Optional<Card> optionalCard = cardRepository.findById(cardId);
            if (optionalCard.isEmpty()) {
                logger.warn("Card with ID {} not found", cardId);
                return false;
            }

            cardRepository.deleteById(cardId);
            logger.info("Card with ID {} deleted successfully", cardId);
            return true;
        } catch (Exception e) {
            logger.error("An error occurred while deleting card with ID {}", cardId, e);
            throw new RuntimeException("An error occurred while deleting card", e);
        }
    }

    private void decryptAndCensureSensitiveData(CardDTO cardDTO) {
        // Decrypt the card number and CVV
        String cardNumber = EncryptionConfig.decrypt(cardDTO.getCardNumber());

        // Mask the card number except the last 4 digits
        String lastNumbers = cardNumber.substring(cardNumber.length() - 4);
        String cardNumberCensured = "**** **** **** " + lastNumbers;

        // Set the masked card number and censored CVV
        cardDTO.setCardNumber(cardNumberCensured);
        cardDTO.setCvv("***");
    }


    private void encryptSensitiveData(Card card) {
        card.setCardNumber(EncryptionConfig.encrypt(card.getCardNumber()));
        card.setCvv(EncryptionConfig.encrypt(card.getCvv()));
    }
}
