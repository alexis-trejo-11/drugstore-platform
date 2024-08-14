package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Payment.CardDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import microservice.ecommerce_payment_service.Service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("v1/api/cards")
public class CardController {

    private final CardService cardService;
    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    public CardController(CardService cardService) {
        this.cardService = cardService;
    }

    @GetMapping("/{cardId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<CardDTO>>> getCardById(@PathVariable Long cardId) {
        return cardService.getCardById(cardId)
                .thenApply(optionalCardDTO -> {
                    if (optionalCardDTO.isEmpty()) {
                        ResponseWrapper<CardDTO> errorResponse = new ResponseWrapper<>(false, null, "Card with ID " + cardId + " not found", 404);
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    }

                    CardDTO dto = optionalCardDTO.get();
                    ResponseWrapper<CardDTO> successResponse = new ResponseWrapper<>(true, dto, "Card Retrieved Successfully!", 200);
                    return ResponseEntity.status(HttpStatus.OK).body(successResponse);
                });

    }
}

