package microservice.employee_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeUpdateDTO;
import microservice.employee_service.Service.EmployeeServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/employees")
public class EmployeeController {

    private final EmployeeServiceImpl employeeService;


    @Autowired
    public EmployeeController(EmployeeServiceImpl employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<String>> addEmployee(@RequestBody @Valid EmployeInsertDTO employeeDTO, BindingResult bindingResult) {
        // Validation Using DTO Config
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("\n");
            });
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(errorMessage.toString()));
        }

        // Employee Creation Using Service Layer
        return CompletableFuture.supplyAsync(() -> {
            employeeService.addEmployee(employeeDTO);

            return ResponseEntity.ok().body("Created");
        }).exceptionally(ex -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build());

    }

    @GetMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<EmployeeDTO>>>> getAllEmployees() {
        return employeeService.getAllEmployees()
                .thenApply(employeeDTOS -> {
                    ResponseWrapper<List<EmployeeDTO>> errorResponse = new ResponseWrapper<>(employeeDTOS, null);
                    return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                })
                .exceptionally(ex -> {
                    ResponseWrapper<List<EmployeeDTO>> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @GetMapping("/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<EmployeeDTO>>> getEmployeeById(@PathVariable Long id) {
        return employeeService.getEmployeeById(id)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<EmployeeDTO> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<EmployeeDTO> response = new ResponseWrapper<>(result.getData(), null);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<EmployeeDTO> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }

    @PutMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> updateEmployee(@RequestBody EmployeeUpdateDTO employeeUpdateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            StringBuilder errorMessage = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                errorMessage.append(error.getDefaultMessage()).append("\n");
            });
            ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, errorMessage.toString());
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse));
        }

        return employeeService.updateEmployee(employeeUpdateDTO)
          .thenApply(result -> {
            if (!result.isSuccess()) {
                ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
            } else {
                ResponseWrapper<Void> response = new ResponseWrapper<>(result.getData(), null);
                return ResponseEntity.status(HttpStatus.OK).body(response);
            }
        })
                .exceptionally(ex -> {
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }


    @DeleteMapping("/{id}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteEmployee(@PathVariable Long employeeId) {
        return employeeService.deleteEmployee(employeeId)
                .thenApply(result -> {
                    if (!result.isSuccess()) {
                        ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage());
                        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse);
                    } else {
                        ResponseWrapper<Void> response = new ResponseWrapper<>(result.getData(), null);
                        return ResponseEntity.status(HttpStatus.OK).body(response);
                    }
                })
                .exceptionally(ex -> {
                    ResponseWrapper<Void> errorResponse = new ResponseWrapper<>(null, ex.getMessage());
                    return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                });
    }
}
