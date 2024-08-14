package microservice.employee_service.Controller;

import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.Postion.PositionUpdateDTO;
import lombok.extern.slf4j.Slf4j;
import microservice.employee_service.Service.PositionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("v1/api/employees/positions")
public class PositionController {

    private final PositionService positionService;

    @Autowired
    public PositionController(PositionService positionService) {
        this.positionService = positionService;
    }

    @PostMapping("/admin/create")
    public ResponseEntity<ResponseWrapper<?>> createPosition(@Valid @RequestBody PositionInsertDTO positionInsertDTO) {
        positionService.createPosition(positionInsertDTO);
        log.info("Position successfully created.");
        return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, null, "Position Successfully Created.", HttpStatus.CREATED.value()));
    }

    @GetMapping("/admin/all")
    public ResponseEntity<ResponseWrapper<List<PositionDTO>>> getAllPositions() {
        List<PositionDTO> positions = positionService.getAllPositions();
        log.info("Fetched all positions.");
        return ResponseEntity.ok(new ResponseWrapper<>(true, positions, "Positions Successfully Fetched.", HttpStatus.OK.value()));
    }

    @GetMapping("/{positionId}")
    public ResponseEntity<ResponseWrapper<PositionDTO>> getPositionById(@PathVariable Long positionId) {
        PositionDTO positionDTO = positionService.getPositionById(positionId);
        if (positionDTO == null) {
            log.warn("Position with ID {} not found.", positionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Position with Id " + positionId + " Not Found.", HttpStatus.NOT_FOUND.value()));
        }
        log.info("Fetched position with ID {}.", positionId);
        return ResponseEntity.ok(new ResponseWrapper<>(true, positionDTO, "Position Successfully Fetched.", HttpStatus.OK.value()));
    }

    @PutMapping("/admin/update")
    public ResponseEntity<ResponseWrapper<?>> updatePosition(@Valid @RequestBody PositionUpdateDTO positionUpdateDTO) {
        boolean isPositionUpdated = positionService.updatePosition(positionUpdateDTO);
        if (!isPositionUpdated) {
            log.warn("Position with ID {} not found for update.", positionUpdateDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Position with Id " + positionUpdateDTO.getId() + " Not Found.", HttpStatus.NOT_FOUND.value()));
        }
        log.info("Position with ID {} successfully updated.", positionUpdateDTO.getId());
        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Position Successfully Updated.", HttpStatus.OK.value()));
    }


    @DeleteMapping("/{positionId}")
    public ResponseEntity<ResponseWrapper<Void>> deletePosition(@PathVariable Long positionId) {
        PositionDTO positionDTO = positionService.getPositionById(positionId);
        if (positionDTO == null) {
            log.warn("Position with ID {} not found for deletion.", positionId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new ResponseWrapper<>(false, null, "Position with Id " + positionId + " Not Found.", HttpStatus.NOT_FOUND.value()));
        }

        positionService.deletePosition(positionId);
        log.info("Position with ID {} successfully deleted.", positionId);
        return ResponseEntity.ok(new ResponseWrapper<>(true, null, "Position Successfully Deleted.", HttpStatus.OK.value()));
    }
}
