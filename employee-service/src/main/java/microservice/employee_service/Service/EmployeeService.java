package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_models.Utils.Result;

import java.util.List;

public interface EmployeeService {
    void addEmployee(EmployeInsertDTO employeeDTO);
    List<EmployeeDTO> getAllEmployees();
    EmployeeDTO getEmployeeById(Long id);
    Result<Void> updateEmployee(EmployeeUpdateDTO employeeUpdateDTO);
    void deleteEmployee(Long id);
}
