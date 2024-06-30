package microservice.ecommerce_payment_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardDTO;
import at.backend.drugstore.microservice.common_models.DTO.Payment.CardInsertDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.ecommerce_payment_service.Service.CardService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("v1/api/cards")
public class CardController {

    private final CardService cardService;
    private final ExternalClientService externalClientService;
    private static final Logger logger = LoggerFactory.getLogger(CardController.class);

    @Autowired
    public CardController(CardService cardService, ExternalClientService externalClientService) {
        this.cardService = cardService;
        this.externalClientService = externalClientService;
    }

    /**
     * Adds a card to the client's account.
     *
     * @param cardInsertDTO The DTO containing card details.
     * @param bindingResult The result of the validation of the DTO.
     * @return ResponseEntity with a status message.
     */
    @PostMapping("/add")
    public ResponseEntity<ResponseWrapper<Void>> addCard(@Valid @RequestBody CardInsertDTO cardInsertDTO, BindingResult bindingResult) {
        try {
            if (bindingResult.hasErrors()) {
                String errorMessages = bindingResult.getAllErrors().stream()
                        .map(ObjectError::getDefaultMessage)
                        .collect(Collectors.joining(", "));
                ResponseWrapper<Void> responseWrapper = new ResponseWrapper<>(null, errorMessages);
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseWrapper);
            }

            Result<ClientDTO> clientDTOResult = externalClientService.findClientById(cardInsertDTO.getClientId());
            if (!clientDTOResult.isSuccess()) {
                ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, clientDTOResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            cardService.addCardToClient(cardInsertDTO);

            ResponseWrapper<Void> response = new ResponseWrapper<>(null, "Card Successfully Added!");
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            logger.error("Error occurred while adding card", e);
            ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, "An error occurred while adding the card");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves all cards associated with a client.
     *
     * @param clientId The ID of the client.
     * @return ResponseEntity with a list of cards and a status message.
     */
    @GetMapping("/client/{clientId}")
    public ResponseEntity<ResponseWrapper<List<CardDTO>>> getCardsByClientId(@PathVariable Long clientId) {
        try {
            Result<ClientDTO> clientDTOResult = externalClientService.findClientById(clientId);
            if (!clientDTOResult.isSuccess()) {
                ResponseWrapper<List<CardDTO>> errorResponse = new ResponseWrapper<>(null, clientDTOResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            List<CardDTO> cardDTOS = cardService.getCardByClientId(clientId);

            ResponseWrapper<List<CardDTO>> response = new ResponseWrapper<>(cardDTOS, "Cards Retrieved Successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving cards", e);
            ResponseWrapper<List<CardDTO>> errorResponse = new ResponseWrapper<>(null, "An error occurred while retrieving the cards");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Retrieves a specific card by ID.
     *
     * @param cardId The ID of the card.
     * @return ResponseEntity with the card DTO and a status message.
     */
    @GetMapping("/{cardId}")
    public ResponseEntity<ResponseWrapper<CardDTO>> getCardById(@PathVariable Long cardId) {
        try {
            CardDTO cardDTO = cardService.getCardById(cardId);
            if (cardDTO == null) {
                ResponseWrapper<CardDTO> errorResponse = new ResponseWrapper<>(null, "Card with ID " + cardId + " not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            ResponseWrapper<CardDTO> response = new ResponseWrapper<>(cardDTO, "Card Retrieved Successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred while retrieving card", e);
            ResponseWrapper<CardDTO> errorResponse = new ResponseWrapper<>(null, "An error occurred while retrieving the card");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    /**
     * Deletes a card by ID.
     *
     * @param cardId   The ID of the card to delete.
     * @param clientId The ID of the client associated with the card.
     * @return ResponseEntity with a status message.
     */
    @DeleteMapping("/{cardId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteCardById(@PathVariable Long cardId, @RequestParam Long clientId) {
        try {
            Result<ClientDTO> clientDTOResult = externalClientService.findClientById(clientId);
            if (!clientDTOResult.isSuccess()) {
                ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, clientDTOResult.getErrorMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            boolean isCardDeleted = cardService.deleteCardById(cardId);
            if (!isCardDeleted) {
                ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, "Card with ID " + cardId + " not found");
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            }

            ResponseWrapper<Void> response = new ResponseWrapper<>(null, "Card Deleted Successfully!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            logger.error("Error occurred while deleting card", e);
            ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, "An error occurred while deleting the card");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}

