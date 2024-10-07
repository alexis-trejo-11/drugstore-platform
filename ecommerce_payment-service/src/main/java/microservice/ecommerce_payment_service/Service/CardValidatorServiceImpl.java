package microservice.ecommerce_payment_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.transaction.Transactional;
import microservice.ecommerce_payment_service.Config.EncryptionConfig;
import microservice.ecommerce_payment_service.Model.Card;
import microservice.ecommerce_payment_service.Repository.CardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class CardValidatorServiceImpl implements  CardValidatorService {

    public final ClientFacadeService clientFacadeService;
    public final CardRepository cardRepository;

    @Autowired
    public CardValidatorServiceImpl(ClientFacadeService clientFacadeService,
                                    CardRepository cardRepository) {
        this.clientFacadeService = clientFacadeService;
        this.cardRepository = cardRepository;
    }

    @Override
    @Transactional
    public boolean validateClient(Long clientId) {
        CompletableFuture<ClientDTO> clientResultAsync = clientFacadeService.getClientById(clientId);
        ClientDTO clientDTO = clientResultAsync.join();
        return clientDTO != null;
    }

    @Override
    @Transactional
    public boolean validateCardData(Long cardId, Long clientId) {
        List<Card> cards = cardRepository.findByClientId(clientId);
        return cards.stream().anyMatch(card -> card.getId().equals(cardId));
    }

    @Override
    public boolean validateExistingCard(Long cardId) {
        return cardRepository.findById(cardId).isPresent();
    }


    @Async("taskExecutor")
    private CompletableFuture<Boolean> validateNotDupliquedCard(String requestedCardNumber, Long clientId) {
        return  CompletableFuture.supplyAsync(() -> {
            List<Card> cards = cardRepository.findByClientId(clientId);

            // Decrypt card numbers from each Card entity
            List<String> decryptedCardNumbers = cards.stream()
                    .map(card -> EncryptionConfig.decrypt(card.getCardNumber()))
                    .toList();

            // Check if any decrypted card number matches the requested card number
            Optional<String> optionalCardNumber = decryptedCardNumbers.stream()
                    .filter(cardNumber -> cardNumber.equals(requestedCardNumber))
                    .findAny();

            return optionalCardNumber.isEmpty();
        });
    }

    @Async("taskExecutor")
    private CompletableFuture<Boolean>  validateMaxNumberOfCards(Long clientId) {
        return CompletableFuture.supplyAsync(() -> {
            List<Card> cards = cardRepository.findByClientId(clientId);
            return cards.size() < 5;
        });
    }


    @Override
    public boolean validateCard(String cardNumber) {
        // Validate card number using Luhn algorithm
        int sum = 0;
        boolean alternate = false;

        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int n = Integer.parseInt(cardNumber.substring(i, i + 1));

            if (alternate) {
                n *= 2;
                if (n > 9) {
                    n = (n % 10) + 1;
                }
            }
            sum += n;
            alternate = !alternate;
        }
        return (sum % 10 == 0);
    }

    @Override
    public Result<Void> validateNewCard(String requestedCardNumber, Long clientId) {
        CompletableFuture<Boolean> cardValidationFuture = validateMaxNumberOfCards(clientId);
        CompletableFuture<Boolean> cardDuplicatedFuture = validateNotDupliquedCard(requestedCardNumber,clientId);

        CompletableFuture.allOf(cardValidationFuture, cardDuplicatedFuture);
        boolean isCardSlotAvalaible = cardValidationFuture.join();
        boolean isCardNumberNotDuplicated  = cardDuplicatedFuture.join();

        if (!isCardSlotAvalaible) {
            return Result.error("Max Number Of Cards Reached.");
        } else if (!isCardNumberNotDuplicated) {
            return Result.error("Card Already Added.");
        } else {
            return Result.success();
        }
    }
}
