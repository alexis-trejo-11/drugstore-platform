package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/client-cards")
@Tag(name = "Drugstore Microservice API (E-Payment Service)", description = "Service for managing client cards")
public class ClientCardController {

    private final CardService cardService;

    public ClientCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Add a card to a client",
            description = "Validates the client and adds a card to their account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Card successfully added"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @PostMapping("/add")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> addCard(@Valid @RequestBody CardInsertDTO cardInsertDTO) {
        log.info("Request to add card for client ID: {}", cardInsertDTO.getClientId());

        return cardService.validateClient(cardInsertDTO.getClientId())
                .thenCompose(isClientValidated -> {
                    if (!isClientValidated) {
                        log.warn("Client with ID {} not found.", cardInsertDTO.getClientId());
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "User with ID " + cardInsertDTO.getClientId() + " Not Found.", 404))
                        );
                    }

                    return cardService.addCardToClient(cardInsertDTO)
                            .thenApply(v -> {
                                log.info("Card successfully added for client ID: {}", cardInsertDTO.getClientId());
                                return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, null, "Card Successfully Added!", 201));
                            });
                });
    }

    @Operation(summary = "Retrieve cards by client ID",
            description = "Fetches all cards associated with a specific client.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Cards retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @GetMapping("/client/{clientId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<CardDTO>>>> getCardsByClientId(@PathVariable Long clientId) {
        log.info("Request to retrieve cards for client ID: {}", clientId);

        return cardService.validateClient(clientId)
                .thenCompose(isClientValidated -> {
                    if (!isClientValidated) {
                        log.warn("Client with ID {} not found.", clientId);
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "User with ID " + clientId + " Not Found.", 404))
                        );
                    }

                    return cardService.getCardByClientId(clientId)
                            .thenApply(cardDTOS -> {
                                log.info("Cards retrieved successfully for client ID: {}", clientId);
                                return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, cardDTOS, "Cards Retrieved Successfully!", 200));
                            });
                });
    }

    @Operation(summary = "Delete a card by ID",
            description = "Deletes a specific card from the client's account.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card deleted successfully"),
            @ApiResponse(responseCode = "400", description = "Card not found"),
            @ApiResponse(responseCode = "404", description = "Client not found")
    })
    @DeleteMapping("/{cardId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteCardById(@PathVariable Long cardId, @RequestParam Long clientId) {
        log.info("Request to delete card ID: {} for client ID: {}", cardId, clientId);

        return cardService.validateClient(clientId)
                .thenCompose(isClientValidated -> {
                    if (!isClientValidated) {
                        log.warn("Client with ID {} not found.", clientId);
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "User with ID " + clientId + " Not Found.", 404))
                        );
                    }

                    return cardService.deleteCardById(cardId)
                            .thenApply(isCardDeleted -> {
                                if (!isCardDeleted) {
                                    log.warn("Card with ID {} not found.", cardId);
                                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ResponseWrapper<>(false, null, "Card with ID " + cardId + " not found", 400));
                                }
                                log.info("Card with ID {} successfully deleted for client ID: {}", cardId, clientId);
                                return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Card Deleted Successfully!", 200));
                            });
                });
    }
}
