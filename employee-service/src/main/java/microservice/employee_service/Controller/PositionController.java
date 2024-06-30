package microservice.employee_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionReturnDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.Postion.PositionUpdateDTO;
import microservice.employee_service.Service.PositionServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/positions")
public class PositionController {

    private final PositionServiceImpl positionService;

    @Autowired
    public PositionController(PositionServiceImpl positionService) {
        this.positionService = positionService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper>> createPosition(@RequestBody PositionInsertDTO positionInsertDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("\n");
            });
            ResponseWrapper response = new ResponseWrapper<>(null, errorMessage.toString());
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(response));
        }

        return CompletableFuture.supplyAsync(() -> {
            positionService.createPosition(positionInsertDTO);
            ResponseWrapper responseWrapper = new ResponseWrapper("Position Successfully Created", null );
            return ResponseEntity.status(201).body(responseWrapper);
        }).exceptionally(ex -> ResponseEntity.status(500).build());
    }

    @GetMapping
    public ResponseEntity<List<PositionReturnDTO>> getAllPositions() {
        List<PositionReturnDTO> positions = positionService.getAllPositions();
        return ResponseEntity.ok(positions);
    }

    @GetMapping("/{positionId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper>> getPositionById(@PathVariable Long positionId) {
        return CompletableFuture.supplyAsync(() -> {
           Result result = positionService.getPositionById(positionId);
           if (!result.isSuccess()) {
               ResponseWrapper responseWrapper = new ResponseWrapper(null, result.getErrorMessage());
               return ResponseEntity.status(404).body(responseWrapper);
           } else {
               ResponseWrapper responseWrapper = new ResponseWrapper( result.getData(), null);
               return ResponseEntity.status(302).body(responseWrapper);
           }
        }).exceptionally(ex -> ResponseEntity.status(500).build());

    }

    @PutMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper>> updatePosition(@RequestBody PositionUpdateDTO positionUpdateDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Result<PositionReturnDTO> result = positionService.updatePosition(positionUpdateDTO);
            if (!result.isSuccess()) {
                ResponseWrapper responseWrapper = new ResponseWrapper(null, result.getErrorMessage());
                return ResponseEntity.status(404).body(responseWrapper);
            } else {
                ResponseWrapper responseWrapper = new ResponseWrapper( result.getData(), null);
                return ResponseEntity.status(200).body(responseWrapper);
            }
        }).exceptionally(ex -> ResponseEntity.status(500).build());

    }

    @DeleteMapping("/{positionId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper>> deletePosition(@PathVariable Long positionId) {
        return CompletableFuture.supplyAsync(() -> {
            Result result = positionService.deletePosition(positionId);
            if (!result.isSuccess()) {
                ResponseWrapper responseWrapper = new ResponseWrapper(null, result.getErrorMessage());
                return ResponseEntity.status(404).body(responseWrapper);
            } else {
                ResponseWrapper responseWrapper = new ResponseWrapper("Position Successfully Deleted", null);
                return ResponseEntity.status(200).body(responseWrapper);
            }
        }).exceptionally(ex -> ResponseEntity.status(500).build());
    }
}
