package microservice.employee_service.Service;


import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_models.DTO.Employee.EmployeeUpdateDTO;
import microservice.employee_service.Model.Employee;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Repository.EmployeeRepository;

import microservice.employee_service.Repository.PositionRepository;
import microservice.employee_service.Utils.CompanyHelper;
import microservice.employee_service.Utils.ModelTransform;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class EmployeeServiceImpl {

    private final EmployeeRepository employeeRepository;
    private final CompanyHelper companyHelper;
    private final PositionRepository positionRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository, CompanyHelper companyHelper, PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.companyHelper = companyHelper;
        this.positionRepository = positionRepository;
    }

    @Async
    @Transactional
    public CompletableFuture<EmployeeDTO> addEmployee(EmployeInsertDTO employeeDTO) {
        try {
            Employee employee = new Employee(employeeDTO);

            String employeeEmail = companyHelper.companyEmailGenerator(employee.getFirstName(), employee.getLastName());
            employee.setCompanyEmail(employeeEmail);

            employeeRepository.saveAndFlush(employee);

            String phoneNumber = companyHelper.getAndAssignCompanyPhone(employee);
            if (phoneNumber != null) {
                employee.setCompanyPhone(phoneNumber);
            }

            employeeRepository.saveAndFlush(employee);

            EmployeeDTO employeeReturnDTO = ModelTransform.employeeToReturnDTO(employee);
            return CompletableFuture.completedFuture(employeeReturnDTO);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Async
    @Transactional
    public CompletableFuture<List<EmployeeDTO>> getAllEmployees() {
        try {
            List<Employee> employees = employeeRepository.findAll();

            List<EmployeeDTO> employeeDTOS = employees.stream()
                    .map(ModelTransform::employeeToReturnDTO)
                    .collect(Collectors.toList());

            return CompletableFuture.completedFuture(employeeDTOS);
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Getting Employees"));
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<EmployeeDTO>> getEmployeeById(Long id) {
        try {
            Optional<Employee> employee = employeeRepository.findById(id);
            if (employee.isPresent()) {
                EmployeeDTO employeeResponseDTO =  ModelTransform.employeeToReturnDTO(employee.get());
                return CompletableFuture.completedFuture(Result.success(employeeResponseDTO));
            } else  {
                return CompletableFuture.completedFuture(Result.error("Employee Not Found"));
            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Getting Employee"));
        }
    }

    @Async
    @Transactional
    public  CompletableFuture<Result<Void>> updateEmployee(EmployeeUpdateDTO employeeUpdateDTO) {
        try {
            Optional<Employee> employee = employeeRepository.findById(employeeUpdateDTO.getId());
            if (employee.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Employee With Id: " + employeeUpdateDTO.getId() + "Not Found"));
            }

            Employee existingEmployee = employee.get();
            updateEmployeeFields(existingEmployee, employeeUpdateDTO);

            if(employeeUpdateDTO.getPositionId() != null) {
               Optional<Position> position = positionRepository.findById(employeeUpdateDTO.getPositionId());
                if (position.isEmpty()) {
                    return CompletableFuture.completedFuture(Result.error("Position With Id: " + employeeUpdateDTO.getPositionId() + " Not Found"));
                } else {
                    employee.get().setPosition(position.get());
                }
            }

            employeeRepository.saveAndFlush(existingEmployee);

            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("An Error Occurred While Updating Employee"));
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<Void>> deleteEmployee(Long id) {
        try {
            // Find the existing employee by ID
            Optional<Employee> employee = employeeRepository.findById(id);
            if (employee.isEmpty()) {
                return CompletableFuture.completedFuture(Result.error("Employee With Id: " + id + "Not Found"));
            }

            // Delete the employee from the database
            employeeRepository.deleteById(id);

            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
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

