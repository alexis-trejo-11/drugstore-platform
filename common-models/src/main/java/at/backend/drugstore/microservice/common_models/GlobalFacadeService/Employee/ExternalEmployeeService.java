package at.backend.drugstore.microservice.common_models.GlobalFacadeService.Employee;

import at.backend.drugstore.microservice.common_models.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Sale.SaleProductsDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public interface ExternalEmployeeService {
    CompletableFuture<Result<EmployeeDTO>> getEmployeeBySaleProductsDTO(SaleProductsDTO saleProductsDTO);
    CompletableFuture<Result<EmployeeDTO>> findEmployeeById(Long employeeId);
}
