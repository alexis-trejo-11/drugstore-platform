package microservice.users.core.ports.output;

import microservice.users.core.models.Employee;
import microservice.users.core.models.valueobjects.EmployeeId;

import java.util.List;

public interface EmployeeRepository extends CommonRepository<Employee, EmployeeId> {
    List<Employee> findByDepartment(String department);
    List<Employee> findByNameContainingIgnoreCase(String name);
}
