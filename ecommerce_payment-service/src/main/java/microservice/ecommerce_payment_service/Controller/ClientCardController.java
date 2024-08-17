package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import microservice.ecommerce_payment_service.Service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("v1/api/client-cards")
public class ClientCardController {

    private final CardService cardService;

    public ClientCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> addCard(@Valid @RequestBody CardInsertDTO cardInsertDTO) {
        return cardService.validateClient(cardInsertDTO.getClientId())
                .thenCompose(isClientValidated -> {
                    if (!isClientValidated) {
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "User with ID " + cardInsertDTO.getClientId() + " Not Found.", 404))
                        );
                    }

                    return cardService.addCardToClient(cardInsertDTO)
                            .thenApply(v -> ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, null, "Card Successfully Added!", 201)));
                });
    }

    @GetMapping("/client/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<CardDTO>>>> getCardsByClientId(@PathVariable Long clientId) {
        return cardService.validateClient(clientId)
                .thenCompose(isClientValidated -> {
                    if (!isClientValidated) {
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<List<CardDTO>>(false, null, "User with ID " + clientId + " Not Found.", 404))
                        );
                    }

                    return cardService.getCardByClientId(clientId)
                            .thenApply(cardDTOS -> ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, cardDTOS, "Cards Retrieved Successfully!", 200)));
                });
    }

    @DeleteMapping("/{cardId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteCardById(@PathVariable Long cardId, @RequestParam Long clientId) {
        return cardService.validateClient(clientId)
                .thenCompose(isClientValidated -> {
                    if (!isClientValidated) {
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<Void>(false, null, "User with ID " + clientId + " Not Found.", 404))
                        );
                    }

                    return cardService.deleteCardById(cardId)
                            .thenApply(isCardDeleted -> {
                                if (!isCardDeleted) {
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<Void>(false, null, "Card with ID " + cardId + " not found", 400));
                                }
                                return ResponseEntity.ok(new ResponseWrapper<Void>(true, null, "Card Deleted Successfully!", 200));
                            });
                });
    }
}