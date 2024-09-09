package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeUpdateDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface EmployeeService {
    void addEmployee(EmployeeInsertDTO employeeDTO);
    Page<EmployeeDTO> getEmployeesByPagesSortedByName(Pageable pageable);
    EmployeeDTO getEmployeeById(Long employeeId);
    void updateEmployee(EmployeeUpdateDTO employeeUpdateDTO);
    void deleteEmployee(Long employeeId);
    boolean validateExisitingEmployee(Long employeeId);
}
