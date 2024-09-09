package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface EmployeeService {
    void addEmployee(EmployeeInsertDTO employeeDTO);
    Page<EmployeeDTO> getEmployeesByPagesSortedByName(Pageable pageable);
    EmployeeDTO getEmployeeById(Long employeeId);
    void updateEmployee(EmployeeUpdateDTO employeeUpdateDTO);
    void deleteEmployee(Long employeeId);
    boolean validateExisitingEmployee(Long employeeId);
    Result<EmployeeDTO> getEmployeeByEmailOrPhoneOrID(RequestEmployeeUser requestEmployeeUser);
}
