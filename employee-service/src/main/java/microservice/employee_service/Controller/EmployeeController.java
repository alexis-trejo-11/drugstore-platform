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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("v1/drugstore/employees")
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
    public ResponseEntity<ResponseWrapper<Void>> addEmployee(@RequestBody @Valid EmployeeInsertDTO employeeDTO) {
        employeeService.addEmployee(employeeDTO);
        return ResponseEntity.ok(ResponseWrapper.created("Employee"));
    }

    @Operation(summary = "Get all employees")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees successfully fetched")
    })
    @GetMapping
    public ResponseEntity<ResponseWrapper<Page<EmployeeDTO>>> getEmployeesByPagesSortedByName(@RequestParam(defaultValue = "0") int page,
                                                                                              @RequestParam(defaultValue = "10") int size) {

        Pageable pageable = PageRequest.of(page, size);
        Page<EmployeeDTO> employeeDTOS = employeeService.getEmployeesByPagesSortedByName(pageable);
        return ResponseEntity.ok(ResponseWrapper.found(employeeDTOS, "Employees"));
    }

    @Operation(summary = "Get employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @GetMapping("/{employeeId}")
    public ResponseEntity<ResponseWrapper<EmployeeDTO>> getEmployeeById(@PathVariable Long employeeId) {
        log.info("Request to fetch employee with ID: {}", employeeId);

        boolean isEmployeeExisting = employeeService.validateExisitingEmployee(employeeId);
        if (!isEmployeeExisting) {
            log.warn("getEmployeeById -> Employee with Id: {} not found", employeeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Employee", "Id"));
        }

        EmployeeDTO employeeDTO = employeeService.getEmployeeById(employeeId);
        log.info("Employee successfully fetched with ID: {}", employeeId);

        return ResponseEntity.ok(ResponseWrapper.found(employeeDTO, "Employee"));
    }

    @Operation(summary = "Update an existing employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully updated"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping
    public ResponseEntity<ResponseWrapper<Void>> updateEmployee(@RequestBody @Valid EmployeeUpdateDTO employeeUpdateDTO) {
        log.info("updateEmployee -> Request to update employee with ID: {}", employeeUpdateDTO.getId());

        boolean isEmployeeExisting = employeeService.validateExisitingEmployee(employeeUpdateDTO.getId());
        if (!isEmployeeExisting) {
            log.warn("updateEmployee -> Employee with Id: {} not found", employeeUpdateDTO.getId());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Employee", "Id"));
        }

        employeeService.updateEmployee(employeeUpdateDTO);
        log.info("updateEmployee -> Employee successfully updated with ID: {}", employeeUpdateDTO.getId());

        return ResponseEntity.ok(ResponseWrapper.ok("Employee", "Update"));
    }

    @Operation(summary = "Delete employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("/{employeeId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteEmployee(@PathVariable Long employeeId) {
        log.info("deleteEmployee -> Request to delete employee with ID: {}", employeeId);

        boolean isEmployeeExisting = employeeService.validateExisitingEmployee(employeeId);
        if (!isEmployeeExisting) {
            log.warn("deleteEmployee -> Employee with Id: {} not found", employeeId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Employee", "Id"));
        }

        employeeService.deleteEmployee(employeeId);
        log.info("deleteEmployee -> Employee successfully deleted with ID: {}", employeeId);

        return ResponseEntity.ok(ResponseWrapper.ok("Employee", "Delete"));
    }
}
