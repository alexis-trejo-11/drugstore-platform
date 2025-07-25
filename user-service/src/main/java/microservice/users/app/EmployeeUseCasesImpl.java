package microservice.users.app;

import lombok.RequiredArgsConstructor;
import microservice.users.core.models.Employee;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.EmployeeId;
import microservice.users.core.ports.input.EmployeeUseCases;
import microservice.users.core.ports.output.EmployeeRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class EmployeeUseCasesImpl implements EmployeeUseCases {
    private final EmployeeRepository employeeRepository;

    @Override
    public Employee createEmployee(Employee employee) {
        if (employeeRepository.findByEmail(employee.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Employee with email already exists");
        }
        return employeeRepository.save(employee);
    }

    @Override
    public Optional<Employee> getEmployeeById(EmployeeId id) {
        return employeeRepository.findById(id);
    }

    @Override
    public Optional<Employee> getEmployeeByEmail(Email email) {
        return employeeRepository.findByEmail(email);
    }

    @Override
    public List<Employee> getAllEmployees() {
        return employeeRepository.findAll();
    }

    @Override
    public Employee updateEmployee(Employee employee) {
        if (!employeeRepository.existsById(employee.getId())) {
            throw new IllegalArgumentException("Employee not found");
        }
        return employeeRepository.save(employee);
    }

    @Override
    public void deleteEmployee(EmployeeId id) {
        if (!employeeRepository.existsById(id)) {
            throw new IllegalArgumentException("Employee not found");
        }
        employeeRepository.delete(id);
    }

    @Override
    public List<Employee> getEmployeesByDepartment(String department) {
        return employeeRepository.findByDepartment(department);
    }

    @Override
    public Employee updateDepartment(EmployeeId id, String department) {
        Optional<Employee> employeeOpt = employeeRepository.findById(id);
        if (employeeOpt.isEmpty()) {
            throw new IllegalArgumentException("Employee not found");
        }
        
        Employee employee = employeeOpt.get();
        // This would require a method in Employee to update department
        return employeeRepository.save(employee);
    }
}
