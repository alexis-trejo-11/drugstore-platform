package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.ecommerce_payment_service.Mappers.CardMapper;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Repository.CardRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
public class CardServiceImpl implements CardService {

    private final CardRepository cardRepository;
    private final CardValidatorService cardValidatorService;
    private final CardMapper cardMapper;
    private final CardDomainService cardDomainService;

    @Autowired
    public CardServiceImpl(CardRepository cardRepository,
                           ClientFacadeService clientFacadeService,
                           CardValidatorService cardValidatorService,
                           CardMapper cardMapper,
                           CardDomainService cardDomainService) {
        this.cardRepository = cardRepository;
        this.cardValidatorService = cardValidatorService;
        this.cardMapper = cardMapper;
        this.cardDomainService = cardDomainService;
    }


    @Override
    @Transactional
    public Result<Void> addCardToClient(CardInsertDTO cardInsertDTO, Long clientId) {
        Card card = cardMapper.insertDtoToEntity(cardInsertDTO, clientId);

        Result<Void> validationReuslt = cardValidatorService.validateNewCard(card.getCardNumber(), clientId);
        if (!validationReuslt.isSuccess()) {
            return Result.error(validationReuslt.getErrorMessage());
        }

        cardDomainService.encryptSensitiveData(card);
        cardRepository.saveAndFlush(card);

        return Result.success();
    }

    @Override
    public CardDTO getCardById(Long cardId) {
        Card card = cardRepository.findById(cardId).orElseThrow(() -> new RuntimeException("Card Not Found"));

        CardDTO cardDTO = cardMapper.entityToDto(card);
        cardDomainService.decryptAndCensureSensitiveData(cardDTO);

        return cardDTO;
    }

    @Override
    public List<CardDTO> getCardByClientId(Long clientId) {
            List<Card> cards = cardRepository.findByClientId(clientId);

            List<CardDTO> cardDTOS = new ArrayList<>();
            for (Card card : cards) {
                CardDTO cardDTO = cardMapper.entityToDto(card);
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
    public Result<Void> updateCard(CardInsertDTO cardInsertDTO, Long clientId, Long cardId) {
        Card cardUpdated = cardMapper.updateDtoToEntity(cardInsertDTO, clientId, cardId);

        Result<Void> validationReuslt = cardValidatorService.validateNewCard(cardUpdated.getCardNumber(), clientId);
        if (!validationReuslt.isSuccess()) {
            return Result.error(validationReuslt.getErrorMessage());
        }

        cardDomainService.encryptSensitiveData(cardUpdated);
        cardRepository.saveAndFlush(cardUpdated);

        return Result.success();
    }

}