package microservice.users.core.ports.input;

import microservice.users.core.models.Employee;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.EmployeeId;

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
