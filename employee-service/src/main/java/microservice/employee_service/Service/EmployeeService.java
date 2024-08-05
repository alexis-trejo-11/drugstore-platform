package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

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
