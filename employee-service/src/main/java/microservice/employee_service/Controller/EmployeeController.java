package microservice.employee_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
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
import java.util.Optional;
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
    @PostMapping("/create")
    public ResponseEntity<ResponseWrapper<Void>> createEmployee(@RequestBody @Valid EmployeeInsertDTO employeeInsertDTO) {
        Result<Void> createResult = employeeService.createEmployee(employeeInsertDTO);
        if (!createResult.isSuccess()) {
             return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(createResult.getErrorMessage()));
        }

        return ResponseEntity.ok(ResponseWrapper.created("Employee"));
    }

    @Operation(summary = "Get Employees Order By Name")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employees successfully fetched")
    })
    @GetMapping("/by-name")
    public ResponseEntity<ResponseWrapper<Page<EmployeeDTO>>> getEmployeesSortedByName(@RequestParam(defaultValue = "0") int page,
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
        Result<EmployeeDTO> employeeDTOResult = employeeService.getEmployeeById(employeeId);
        if (!employeeDTOResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("employee"));
        }

        return ResponseEntity.ok(ResponseWrapper.found(employeeDTOResult.getData(), "Employee"));
    }

    @Operation(summary = "Get employee by Company Data or Id")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully fetched"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PostMapping("/search")
    public ResponseWrapper<EmployeeDTO> getEmployeeForUserCreation(@RequestBody RequestEmployeeUser requestEmployeeUser) {
        Result<EmployeeDTO> employeeDTOResult = employeeService.getEmployeeByEmailOrPhoneOrID(requestEmployeeUser);
        if (!employeeDTOResult.isSuccess()) {
            return ResponseWrapper.error(employeeDTOResult.getErrorMessage(), 404);
        }

        return ResponseWrapper.found(employeeDTOResult.getData(), "Employee");
    }


    @Operation(summary = "Update an existing employee")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully updated"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @PutMapping("/update")
    public ResponseEntity<ResponseWrapper<Void>> updateEmployee(@RequestBody @Valid EmployeeUpdateDTO employeeUpdateDTO) {
        Result<Void> updateResult = employeeService.updateEmployee(employeeUpdateDTO);
        if (!updateResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Employee"));
        }

        return ResponseEntity.ok(ResponseWrapper.ok("Employee", "Update"));
    }

    @Operation(summary = "Delete employee by ID")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Employee successfully deleted"),
            @ApiResponse(responseCode = "404", description = "Employee not found")
    })
    @DeleteMapping("delete/{employeeId}")
    public ResponseEntity<ResponseWrapper<Void>> deleteEmployee(@PathVariable Long employeeId) {
       Result<Void> deleteResult = employeeService.deleteEmployee(employeeId);
       if (!deleteResult.isSuccess()) {
           return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.notFound("Employee"));
       }

       return ResponseEntity.ok(ResponseWrapper.ok("Employee", "Delete"));
    }

    @GetMapping("/validate/{employeeId}")
    public boolean validateExistingEmployee(@PathVariable Long employeeId) {
        return employeeService.validateExisitingEmployee(employeeId);
    }
}
