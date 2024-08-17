package microservice.employee_service.Service;


import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.employee_service.Mappers.EmployeeMapper;
import microservice.employee_service.Model.Employee;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Repository.EmployeeRepository;
import microservice.employee_service.Repository.PositionRepository;
import microservice.employee_service.Utils.CompanyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyHelper companyHelper;
    private final PositionRepository positionRepository;
    private final EmployeeMapper employeeMapper;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               CompanyHelper companyHelper,
                               PositionRepository positionRepository,
                               EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.companyHelper = companyHelper;
        this.positionRepository = positionRepository;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Void> addEmployee(EmployeeInsertDTO employeeDTO) {
        return CompletableFuture.runAsync(() -> {
            Employee employee = new Employee(employeeDTO);

            String employeeEmail = companyHelper.companyEmailGenerator(employee.getFirstName(), employee.getLastName());
            employee.setCompanyEmail(employeeEmail);

            employeeRepository.saveAndFlush(employee);

            String phoneNumber = companyHelper.getAndAssignCompanyPhone(employee);
            if (phoneNumber != null) {
                employee.setCompanyPhone(phoneNumber);
            }

            employeeRepository.saveAndFlush(employee);
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<List<EmployeeDTO>> getAllEmployees() {
        return CompletableFuture.supplyAsync(() -> {
            List<Employee> employees = employeeRepository.findAll();
            return employees.stream()
                    .map(employeeMapper::employeeToDTO)
                    .collect(Collectors.toList());
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Optional<EmployeeDTO>> getEmployeeById(Long id) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Employee> optionalEmployee = employeeRepository.findById(id);
            return optionalEmployee.map(employeeMapper::employeeToDTO);
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Result<Void>> updateEmployee(EmployeeUpdateDTO employeeUpdateDTO) {
        return CompletableFuture.supplyAsync(() -> {
            Optional<Employee> employee = employeeRepository.findById(employeeUpdateDTO.getId());
            if (employee.isEmpty()) {
                return Result.error("Employee With Id: " + employeeUpdateDTO.getId() + " Not Found");
            }

            Employee existingEmployee = employee.get();
            updateEmployeeFields(existingEmployee, employeeUpdateDTO);

            if(employeeUpdateDTO.getPositionId() != null) {
                Optional<Position> position = positionRepository.findById(employeeUpdateDTO.getPositionId());
                if (position.isEmpty()) {
                    return Result.error("Position With Id: " + employeeUpdateDTO.getPositionId() + " Not Found");
                } else {
                    employee.get().setPosition(position.get());
                }
            }

            employeeRepository.saveAndFlush(existingEmployee);

            return Result.success();
        });
    }

    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Void> deleteEmployee(Long id) {
        return CompletableFuture.runAsync(() -> employeeRepository.deleteById(id));
    }

    private void updateEmployeeFields(Employee existingEmployee, EmployeeUpdateDTO employeeUpdateDTO) {
        if (employeeUpdateDTO.getFirstName() != null) {
            existingEmployee.setFirstName(employeeUpdateDTO.getFirstName());
        }
        if (employeeUpdateDTO.getLastName() != null) {
            existingEmployee.setLastName(employeeUpdateDTO.getLastName());
        }
        if (employeeUpdateDTO.getAddress() != null) {
            existingEmployee.setAddress(employeeUpdateDTO.getAddress());
        }
        if (employeeUpdateDTO.getCompanyPhone() != null) {
            existingEmployee.setCompanyPhone(employeeUpdateDTO.getCompanyPhone());
        }
        if (employeeUpdateDTO.getCompanyEmail() != null) {
            existingEmployee.setCompanyEmail(employeeUpdateDTO.getCompanyEmail());
        }

        existingEmployee.setEmployeeActive(employeeUpdateDTO.isEmployeeActive());

        if (employeeUpdateDTO.getBirthDate() != null) {
            existingEmployee.setBirthDate(employeeUpdateDTO.getBirthDate());
        }
        if (employeeUpdateDTO.getFiredAt() != null) {
            existingEmployee.setFiredAt(employeeUpdateDTO.getFiredAt());
        }
    }
}
