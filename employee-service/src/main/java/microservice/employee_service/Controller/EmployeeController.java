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

@RestController
@RequestMapping("v1/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    @Autowired
    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    @PostMapping
    public ResponseEntity<ApiResponse<?>> addEmployee(@RequestBody @Valid EmployeInsertDTO employeeDTO, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
        CustomControllerResponse validationError = ControllerValidation.handleValidationError(bindingResult);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationError, "Validation Error", 400));
        }

        employeeService.addEmployee(employeeDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(false, null, "Employee Successfully Created.", 201));
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<EmployeeDTO>>> getAllEmployees() {
        List<EmployeeDTO> employeeDTOS = employeeService.getAllEmployees();
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(false, employeeDTOS, "Employees Successfully Fetched.", 200));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<EmployeeDTO>> getEmployeeById(@PathVariable Long employeeId) {
        EmployeeDTO employeeDTO = employeeService.getEmployeeById(employeeId);
        if (employeeDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Employee With " + employeeId + " Not Found", 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(false, employeeDTO, "Employee Successfully Fetched.", 200));
    }

    @PutMapping
    public ResponseEntity<ApiResponse<?>> updateEmployee(@RequestBody EmployeeUpdateDTO employeeUpdateDTO, BindingResult bindingResult) {
        if (!bindingResult.hasErrors()) {
            CustomControllerResponse validationError = ControllerValidation.handleValidationError(bindingResult);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationError, "Validation Error", 400));
        }

        Result<Void> updateEmployeeResult = employeeService.updateEmployee(employeeUpdateDTO);
        if (!updateEmployeeResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, updateEmployeeResult.getErrorMessage(), 404));
        }

        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(false, null, "Employee Successfully Updated.", 200));
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> deleteEmployee(@PathVariable Long employeeId) {
        EmployeeDTO employeeDTO = employeeService.getEmployeeById(employeeId);
        if (employeeDTO == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, "Employee With " + employeeId + " Not Found", 404));
        }

        employeeService.deleteEmployee(employeeId);
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(false, null, "Employee Successfully Deleted.", 200));
    }
}
