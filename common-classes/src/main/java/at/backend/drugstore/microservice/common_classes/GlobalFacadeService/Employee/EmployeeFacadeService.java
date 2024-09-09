package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.concurrent.CompletableFuture;

public interface EmployeeFacadeService {
    CompletableFuture<Result<EmployeeDTO>> getEmployeeById(Long employeeId);
    CompletableFuture<Result<EmployeeDTO>> getEmployeeForUserCreation(RequestEmployeeUser requestEmployeeUser);
}
