package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.PaymentInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
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
    private final ExternalClientService externalClientService;
    private final CardMapper cardMapper;
    private static final Logger logger = LoggerFactory.getLogger(CardServiceImpl.class);


    @Autowired
    public CardServiceImpl(CardRepository cardRepository, ExternalClientService externalClientService, CardMapper cardMapper) {
        this.cardRepository = cardRepository;
        this.externalClientService = externalClientService;
        this.cardMapper = cardMapper;
    }

    @Override
    @Async
    @Transactional
    public boolean validateClient(Long clientId) {
        Result<ClientDTO> clientDTOResult = externalClientService.findClientById(clientId);
        return clientDTOResult.isSuccess();
    }

    @Override
    @Async
    @Transactional
    public boolean validateCardData(Long cardId, Long clientId) {
        List<Card> cards = cardRepository.findByClientId(clientId);

        Optional<Card> cardFounded = cards.stream().filter(card -> card.getId().equals(cardId)).findFirst();
        return cardFounded.isPresent();
    }

    @Override
    @Async
    @Transactional
    public void addCardToClient(CardInsertDTO cardInsertDTO) {
            Card card = cardMapper.toEntity(cardInsertDTO);
            encryptSensitiveData(card);
            cardRepository.saveAndFlush(card);
    }

    @Async
    public Optional<CardDTO> getCardById(Long cardId) {
            Optional<Card> optionalCard = cardRepository.findById(cardId);
            if(optionalCard.isEmpty()) {
                return Optional.empty();
            }
            Card card = optionalCard.get();

            CardDTO cardDTO = cardMapper.toDto(card);
            decryptAndCensureSensitiveData(cardDTO);

            return Optional.of(cardDTO);
    }

    @Async
    public List<CardDTO> getCardByClientId(Long clientId) {
            List<Card> cards = cardRepository.findByClientId(clientId);
            if (cards.isEmpty()) {
                return new ArrayList<>();
            }

            List<CardDTO> cardDTOS = new ArrayList<>();
            for (Card card : cards ) {
                CardDTO cardDTO = cardMapper.toDto(card);
                decryptAndCensureSensitiveData(cardDTO);
                cardDTOS.add(cardDTO);
            }
            return cardDTOS;
    }

    @Async
    @Override
    public boolean deleteCardById(Long cardId) {
            Optional<Card> optionalCard = cardRepository.findById(cardId);
            if (optionalCard.isEmpty()) {
                return false;
            }

            cardRepository.deleteById(cardId);
            return true;
    }

    private void decryptAndCensureSensitiveData(CardDTO cardDTO) {
        // Decrypt the card number and CVV
        String cardNumber = EncryptionConfig.decrypt(cardDTO.getCardNumber());

        // Mask the card number except the last 4 digits
        String lastNumbers = cardNumber.substring(cardNumber.length() - 4);
        String cardNumberCensured = "**** **** **** " + lastNumbers;

        cardDTO.setCardNumber(cardNumberCensured);
        cardDTO.setCvv("***");
    }


    private void encryptSensitiveData(Card card) {
        card.setCardNumber(EncryptionConfig.encrypt(card.getCardNumber()));
        card.setCvv(EncryptionConfig.encrypt(card.getCvv()));
    }
}
