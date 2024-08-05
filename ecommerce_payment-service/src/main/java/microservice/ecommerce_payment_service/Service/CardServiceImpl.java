package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
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
import java.util.concurrent.CompletableFuture;

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

    @Async("taskExecutor")
    @Override
    @Transactional
    public CompletableFuture<Boolean> validateClient(Long clientId) {
        return externalClientService.findClientById(clientId)
                .thenApply(Result::isSuccess);
    }
    @Override
    @Transactional
    public CompletableFuture<Boolean> validateCardData(Long cardId, Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Card> cards = cardRepository.findByClientId(clientId);
            return cards.stream().anyMatch(card -> card.getId().equals(cardId));
        });
    }

    @Override
    @Transactional
    public CompletableFuture<Void> addCardToClient(CardInsertDTO cardInsertDTO) {
        return CompletableFuture.runAsync(() -> {
            Card card = cardMapper.toEntity(cardInsertDTO);
            encryptSensitiveData(card);
            cardRepository.saveAndFlush(card);
        });
    }

    @Override
    public CompletableFuture<Optional<CardDTO>> getCardById(Long cardId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Card> optionalCard = cardRepository.findById(cardId);
            return optionalCard.map(card -> {
                CardDTO cardDTO = cardMapper.toDto(card);
                decryptAndCensureSensitiveData(cardDTO);
                return cardDTO;
            });
        });
    }

    @Override
    public CompletableFuture<List<CardDTO>> getCardByClientId(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Card> cards = cardRepository.findByClientId(clientId);
            List<CardDTO> cardDTOS = new ArrayList<>();
            for (Card card : cards) {
                CardDTO cardDTO = cardMapper.toDto(card);
                decryptAndCensureSensitiveData(cardDTO);
                cardDTOS.add(cardDTO);
            }
            return cardDTOS;
        });
    }

    @Override
    @Transactional
    public CompletableFuture<Boolean> deleteCardById(Long cardId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Card> optionalCard = cardRepository.findById(cardId);
            if (optionalCard.isEmpty()) {
                return false;
            }

            cardRepository.deleteById(cardId);
            return true;
        });
    }

    private void decryptAndCensureSensitiveData(CardDTO cardDTO) {
        String cardNumber = EncryptionConfig.decrypt(cardDTO.getCardNumber());
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