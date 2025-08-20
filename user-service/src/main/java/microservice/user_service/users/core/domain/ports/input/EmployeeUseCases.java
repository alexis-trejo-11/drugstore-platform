package microservice.user_service.users.core.domain.ports.input;

import microservice.user_service.users.core.domain.models.valueobjects.Email;
import microservice.user_service.users.core.domain.models.valueobjects.EmployeeId;

import java.util.List;
import java.util.Optional;

public interface EmployeeUseCases {
    Employee createEmployee(Employee employee);
    Optional<Employee> getEmployeeById(EmployeeId id);
    Optional<Employee> getEmployeeByEmail(Email email);
    List<Employee> getAllEmployees();
    Employee updateEmployee(Employee employee);
    void deleteEmployee(EmployeeId id);
    List<Employee> getEmployeesByDepartment(String department);
    Employee updateDepartment(EmployeeId id, String department);
}
