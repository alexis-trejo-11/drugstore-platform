package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_payment_service.Automappers.CardMapper;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Repository.CardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final ClientFacadeService clientFacadeService;
    private final CardMapper cardMapper;
    private final CardDomainService cardDomainService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository,
                           ClientFacadeService clientFacadeService,
                           CardMapper cardMapper,
                           CardDomainService cardDomainService) {
        this.cardRepository = cardRepository;
        this.clientFacadeService = clientFacadeService;
        this.cardMapper = cardMapper;
        this.cardDomainService = cardDomainService;
    }

    @Async("taskExecutor")
    @Override
    @Transactional
    public CompletableFuture<Boolean> validateClient(Long clientId) {
        return clientFacadeService.findClientById(clientId)
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
            cardDomainService.encryptSensitiveData(card);
            cardRepository.saveAndFlush(card);
        });
    }

    @Override
    public CompletableFuture<Optional<CardDTO>> getCardById(Long cardId) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Card> optionalCard = cardRepository.findById(cardId);
            return optionalCard.map(card -> {
                CardDTO cardDTO = cardMapper.toDto(card);
                cardDomainService.decryptAndCensureSensitiveData(cardDTO);
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
                cardDomainService.decryptAndCensureSensitiveData(cardDTO);
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


}