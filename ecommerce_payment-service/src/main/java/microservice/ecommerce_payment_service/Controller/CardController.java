package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.ecommerce_payment_service.Service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

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
    public ResponseEntity<ApiResponse<CardDTO>> getCardById(@PathVariable Long cardId) {
        Optional<CardDTO> optionalCardDTO = cardService.getCardById(cardId);
        if (optionalCardDTO.isEmpty()) {
            ApiResponse<CardDTO> errorResponse = new ApiResponse<>(false, null, "Card with ID " + cardId + " not found", 404);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
        }

        CardDTO dto = optionalCardDTO.get();
        ApiResponse<CardDTO> successResponse = new ApiResponse<>(true, dto, "Card Retrieved Successfully!", 200);
        return ResponseEntity.status(HttpStatus.OK).body(successResponse);
    }
}

