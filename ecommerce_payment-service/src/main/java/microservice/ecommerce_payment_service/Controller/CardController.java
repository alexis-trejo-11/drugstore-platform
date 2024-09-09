package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import microservice.ecommerce_payment_service.Service.CardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/cards")
@Tag(name = "Drugstore Microservice API (E-Payment Service)", description = "Service for managing cards for payments")

public class CardController {

    private final CardService cardService;

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @Operation(summary = "Retrieve card by ID",
            description = "Fetches a card based on its ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Card retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Card not found")
    })
    @GetMapping("/{cardId}")
    public ResponseEntity<ResponseWrapper<CardDTO>> getCardById(@PathVariable Long cardId) {
        log.info("Request to retrieve card with ID: {}", cardId);
        boolean isCartExisting = cardService.validateExistingCard(cardId);
        if (!isCartExisting) {
            log.warn("Card with ID {} not found.", cardId);
            ResponseWrapper<CardDTO> errorResponse = new ResponseWrapper<>(false, null, "Card with ID " + cardId + " not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        CardDTO cardDTO = cardService.getCardById(cardId);
        log.info("Card with ID {} retrieved successfully.", cardId);

        return ResponseEntity.status(HttpStatus.OK).body(ResponseWrapper.found(cardDTO, "Card"));
    }
}