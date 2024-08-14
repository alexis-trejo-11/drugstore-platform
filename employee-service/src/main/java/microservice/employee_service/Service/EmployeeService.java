package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface EmployeeService {
    CompletableFuture<Void> addEmployee(EmployeInsertDTO employeeDTO);
    CompletableFuture<List<EmployeeDTO>> getAllEmployees();
    CompletableFuture<Optional<EmployeeDTO>> getEmployeeById(Long id);
    CompletableFuture<Result<Void>> updateEmployee(EmployeeUpdateDTO employeeUpdateDTO);
    CompletableFuture<Void> deleteEmployee(Long id);
}
