package at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Sale.SaleProductsDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

public interface EmployeeFacadeService {
    CompletableFuture<Result<EmployeeDTO>> getEmployeeBySaleProductsDTO(SaleProductsDTO saleProductsDTO);
    CompletableFuture<Result<EmployeeDTO>> findEmployeeById(Long employeeId);
}
