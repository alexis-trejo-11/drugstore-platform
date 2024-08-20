package microservice.employee_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeInsertDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeUpdateDTO;
import microservice.employee_service.Service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/api/employees")
@Tag(name = "Drugstore Microservice API (Employee Service)", description = "Service for managing employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @Operation(summary = "Add a new employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Employee successfully created"),
            @ApiResponse(responseCode = "400", description = "Invalid input data")
    })
    @PostMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> addEmployee(@RequestBody @Valid EmployeeInsertDTO employeeDTO) {
        log.info("Request to add a new employee");
        return employeeService.addEmployee(employeeDTO).thenApply(voidResult -> {
            log.info("Employee successfully created");
            return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, null, "Employee Successfully Created.", 201));
        });
    }

    @Operation(summary = "Get all employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees successfully fetched")
    })
    @GetMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<List<EmployeeDTO>>>> getAllEmployees() {
        log.info("Request to fetch all employees");
        return employeeService.getAllEmployees().thenApply(employeeDTOS -> {
            log.info("Employees successfully fetched");
            return ResponseEntity.status(HttpStatus.OK).body(new ResponseWrapper<>(true, employeeDTOS, "Employees Successfully Fetched.", 200));
        });
    }

    @Operation(summary = "Get employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{employeeId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<EmployeeDTO>>> getEmployeeById(@PathVariable Long employeeId) {
        log.info("Request to fetch employee with ID: {}", employeeId);
        return employeeService.getEmployeeById(employeeId).thenApply(employeeDTO -> {
            return employeeDTO.map(dto -> {
                log.info("Employee successfully fetched with ID: {}", employeeId);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseWrapper<>(true, dto, "Employee Successfully Fetched.", 200));
            }).orElseGet(() -> {
                log.warn("Employee with ID: {} not found", employeeId);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseWrapper<>(false, null, "Employee With " + employeeId + " Not Found", 404));
            });
        });
    }

    @Operation(summary = "Update an existing employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully updated"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping
    public CompletableFuture<ResponseEntity<ResponseWrapper<?>>> updateEmployee(@RequestBody @Valid EmployeeUpdateDTO employeeUpdateDTO) {
        log.info("Request to update employee with ID: {}", employeeUpdateDTO.getId());
        return employeeService.updateEmployee(employeeUpdateDTO).thenApply(updateEmployeeResult -> {
            if (!updateEmployeeResult.isSuccess()) {
                log.warn("Employee update failed for ID: {}", employeeUpdateDTO.getId());
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseWrapper<>(false, null, updateEmployeeResult.getErrorMessage(), 404));
            }
            log.info("Employee successfully updated with ID: {}", employeeUpdateDTO.getId());
            return ResponseEntity.status(HttpStatus.OK)
                    .body(new ResponseWrapper<>(true, null, "Employee Successfully Updated.", 200));
        });
    }

    @Operation(summary = "Delete employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{employeeId}")
    public CompletableFuture<ResponseEntity<ResponseWrapper<Void>>> deleteEmployee(@PathVariable Long employeeId) {
        log.info("Request to delete employee with ID: {}", employeeId);
        return employeeService.getEmployeeById(employeeId).thenCompose(employeeDTO -> {
            if (employeeDTO.isEmpty()) {
                log.warn("Employee with ID: {} not found", employeeId);
                return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(new ResponseWrapper<>(false, null, "Employee With " + employeeId + " Not Found", 404)));
            }
            return employeeService.deleteEmployee(employeeId).thenApply(voidResult -> {
                log.info("Employee successfully deleted with ID: {}", employeeId);
                return ResponseEntity.status(HttpStatus.OK)
                        .body(new ResponseWrapper<>(true, null, "Employee Successfully Deleted.", 200));
            });
        });
    }
}
