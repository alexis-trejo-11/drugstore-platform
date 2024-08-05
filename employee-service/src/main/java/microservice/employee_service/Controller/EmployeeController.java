package microservice.employee_service.Controller;

import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import at.backend.drugstore.microservice.common_models.Validations.CustomControllerResponse;
import microservice.employee_service.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("v1/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> addEmployee(@RequestBody @Valid EmployeInsertDTO employeeDTO) {
        return employeeService.addEmployee(employeeDTO).thenApply(voidResult ->
                ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, null, "Employee Successfully Created.", 201))
        );
    }

    @GetMapping
    public CompletableFuture<ResponseEntity<ApiResponse<List<EmployeeDTO>>>> getAllEmployees() {
        return employeeService.getAllEmployees().thenApply(employeeDTOS ->
                ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, employeeDTOS, "Employees Successfully Fetched.", 200))
        );
    }

    @GetMapping("/{employeeId}")
    public CompletableFuture<ResponseEntity<ApiResponse<EmployeeDTO>>> getEmployeeById(@PathVariable Long employeeId) {
        return employeeService.getEmployeeById(employeeId).thenApply(employeeDTO -> {
            if (employeeDTO.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(true, null, "Employee With " + employeeId + " Not Found", 404));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(false, employeeDTO.get(), "Employee Successfully Fetched.", 200));
        });
    }

    @PutMapping
    public CompletableFuture<ResponseEntity<ApiResponse<?>>> updateEmployee(@RequestBody @Valid EmployeeUpdateDTO employeeUpdateDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            CustomControllerResponse validationError = ControllerValidation.handleValidationError(bindingResult);
            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationError, "Validation Error", 400)));
        }

        return employeeService.updateEmployee(employeeUpdateDTO).thenApply(updateEmployeeResult -> {
            if (!updateEmployeeResult.isSuccess()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, updateEmployeeResult.getErrorMessage(), 404));
            }
            return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Employee Successfully Updated.", 200));
        });
    }

    @DeleteMapping("/{employeeId}")
    public CompletableFuture<ResponseEntity<ApiResponse<Void>>> deleteEmployee(@PathVariable Long employeeId) {
        return employeeService.getEmployeeById(employeeId).thenCompose(employeeDTO -> {
            if (employeeDTO.isEmpty()) {
                return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Employee With " + employeeId + " Not Found", 404)));
            }
            return employeeService.deleteEmployee(employeeId).thenApply(voidResult ->
                    ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, null, "Employee Successfully Deleted.", 200))
            );
        });
    }
}
