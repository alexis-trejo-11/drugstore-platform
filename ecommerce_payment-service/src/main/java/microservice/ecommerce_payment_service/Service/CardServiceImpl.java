package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_payment_service.Mappers.CardMapper;
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

    @Override
    @Transactional
    public boolean validateClient(Long clientId) {
        CompletableFuture<Result<ClientDTO>> clientResultAsync = clientFacadeService.findClientById(clientId);
        Result<ClientDTO> clientDTOResult = clientResultAsync.join();
        return clientDTOResult.isSuccess();
    }

    @Override
    @Transactional
    public boolean validateCardData(Long cardId, Long clientId) {
        List<Card> cards = cardRepository.findByClientId(clientId);
        return cards.stream().anyMatch(card -> card.getId().equals(cardId));
    }

    @Override
    @Transactional
    public void addCardToClient(CardInsertDTO cardInsertDTO) {
        Card card = cardMapper.toEntity(cardInsertDTO);
        cardDomainService.encryptSensitiveData(card);
        cardRepository.saveAndFlush(card);
    }

    @Override
    public CardDTO getCardById(Long cardId) {
        Card card = cardRepository.findById(cardId).orElse(null);

        CardDTO cardDTO = cardMapper.toDto(card);
        cardDomainService.decryptAndCensureSensitiveData(cardDTO);

        return cardDTO;
    }

    @Override
    public List<CardDTO> getCardByClientId(Long clientId) {
            List<Card> cards = cardRepository.findByClientId(clientId);

            List<CardDTO> cardDTOS = new ArrayList<>();
            for (Card card : cards) {
                CardDTO cardDTO = cardMapper.toDto(card);
                cardDomainService.decryptAndCensureSensitiveData(cardDTO);
                cardDTOS.add(cardDTO);
            }

            return cardDTOS;
    }

    @Override
    @Transactional
    public void deleteCardById(Long cardId) {
            cardRepository.deleteById(cardId);
    }

    @Override
    public boolean validateExistingCard(Long cardId) {
        return cardRepository.findById(cardId).isPresent();
    }

}