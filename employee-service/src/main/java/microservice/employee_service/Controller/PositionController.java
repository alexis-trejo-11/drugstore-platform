package microservice.employee_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionUpdateDTO;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.employee_service.Service.PositionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("v1/api/employees/positions")
public class PositionController {

    private static final Logger logger = LoggerFactory.getLogger(PositionController.class);

    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    /**
     * Create a new position.
     */
    @PostMapping("/admin/create")
    public ResponseEntity<ApiResponse<?>> createPosition(@Valid @RequestBody PositionInsertDTO positionInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            logger.warn("Validation errors occurred while creating position: {}", validationErrors);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, validationErrors, "Validation Error", HttpStatus.BAD_REQUEST.value()));
        }

        positionService.createPosition(positionInsertDTO);
        logger.info("Position successfully created.");
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, null, "Position Successfully Created.", HttpStatus.CREATED.value()));
    }

    /**
     * Get all positions.
     */
    @GetMapping("/admin/all")
    public ResponseEntity<ApiResponse<List<PositionDTO>>> getAllPositions() {
        List<PositionDTO> positions = positionService.getAllPositions();
        logger.info("Fetched all positions.");
        return ResponseEntity.ok(new ApiResponse<>(true, positions, "Positions Successfully Fetched.", HttpStatus.OK.value()));
    }

    /**
     * Get position by ID.
     */
    @GetMapping("/{positionId}")
    public ResponseEntity<ApiResponse<PositionDTO>> getPositionById(@PathVariable Long positionId) {
        PositionDTO positionDTO = positionService.getPositionById(positionId);
        if (positionDTO == null) {
            logger.warn("Position with ID {} not found.", positionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, null, "Position with Id " + positionId + " Not Found.", HttpStatus.NOT_FOUND.value()));
        }
        logger.info("Fetched position with ID {}.", positionId);
        return ResponseEntity.ok(new ApiResponse<>(true, positionDTO, "Position Successfully Fetched.", HttpStatus.OK.value()));
    }

    /**
     * Update an existing position.
     */
    @PutMapping("/admin/update")
    public ResponseEntity<ApiResponse<?>> updatePosition(@Valid @RequestBody PositionUpdateDTO positionUpdateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            logger.warn("Validation errors occurred while updating position: {}", validationErrors);
            return ResponseEntity.badRequest().body(new ApiResponse<>(false, validationErrors, "Validation Error", HttpStatus.BAD_REQUEST.value()));
        }

        boolean isPositionUpdated = positionService.updatePosition(positionUpdateDTO);
        if (!isPositionUpdated) {
            logger.warn("Position with ID {} not found for update.", positionUpdateDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, null, "Position with Id " + positionUpdateDTO.getId() + " Not Found.", HttpStatus.NOT_FOUND.value()));
        }
        logger.info("Position with ID {} successfully updated.", positionUpdateDTO.getId());
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Position Successfully Updated.", HttpStatus.OK.value()));
    }

    /**
     * Delete a position by ID.
     */
    @DeleteMapping("/{positionId}")
    public ResponseEntity<ApiResponse<Void>> deletePosition(@PathVariable Long positionId) {
        PositionDTO positionDTO = positionService.getPositionById(positionId);
        if (positionDTO == null) {
            logger.warn("Position with ID {} not found for deletion.", positionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ApiResponse<>(false, null, "Position with Id " + positionId + " Not Found.", HttpStatus.NOT_FOUND.value()));
        }

        positionService.deletePosition(positionId);
        logger.info("Position with ID {} successfully deleted.", positionId);
        return ResponseEntity.ok(new ApiResponse<>(true, null, "Position Successfully Deleted.", HttpStatus.OK.value()));
    }
}
