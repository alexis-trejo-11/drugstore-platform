package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.ecommerce_payment_service.Service.CardService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("v1/api/client-cards")
public class ClientCardController {

    private final CardService cardService;

    public ClientCardController(CardService cardService) {
        this.cardService = cardService;
    }

    @PostMapping("/add")
    public ResponseEntity<ApiResponse<?>> addCard(@Valid @RequestBody CardInsertDTO cardInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            var validationError = ControllerValidation.handleValidationError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationError, "Validation Error.", 400));
        }

        boolean isClientValidated = cardService.validateClient(cardInsertDTO.getClientId());
        if (!isClientValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "User with ID " + cardInsertDTO.getClientId() + " Not Found.", 404));
        }

        cardService.addCardToClient(cardInsertDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, null, "Card Successfully Added!", 201));
    }

    @GetMapping("/client/{clientId}")
    public ResponseEntity<ApiResponse<List<CardDTO>>> getCardsByClientId(@PathVariable Long clientId) {
        boolean isClientValidated = cardService.validateClient(clientId);
        if (!isClientValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "User with ID " + clientId + " Not Found.", 404));
        }

        List<CardDTO> cardDTOS = cardService.getCardByClientId(clientId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, cardDTOS, "Cards Retrieved Successfully!", 200));
    }

    @DeleteMapping("/{cardId}")
    public ResponseEntity<ApiResponse<Void>> deleteCardById(@PathVariable Long cardId, @RequestParam Long clientId) {
        boolean isClientValidated = cardService.validateClient(clientId);
        if (!isClientValidated) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "User with ID " + clientId + " Not Found.", 404));
        }

        boolean isCardDeleted = cardService.deleteCardById(cardId);
        if (!isCardDeleted) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Card with ID " + cardId + " not found", 400));
        }

        return ResponseEntity.ok(new ApiResponse<>(true, null, "Card Deleted Successfully!", 200));
    }
}
