package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.ws.rs.NotFoundException;
import microservice.employee_service.Mappers.EmployeeMapper;
import microservice.employee_service.Model.Employee;
import microservice.employee_service.Model.Position;
import microservice.employee_service.Repository.EmployeeRepository;
import microservice.employee_service.Repository.PositionRepository;
import microservice.employee_service.Utils.CompanyHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.Optional;

@Service
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final CompanyHelper companyHelper;
    private final EmployeeMapper employeeMapper;
    private final PositionRepository positionRepository;

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               CompanyHelper companyHelper,
                               EmployeeMapper employeeMapper,
                               PositionRepository positionRepository) {
        this.employeeRepository = employeeRepository;
        this.companyHelper = companyHelper;
        this.employeeMapper = employeeMapper;
        this.positionRepository = positionRepository;
    }

    @Override
    @Transactional
    public Result<Void> createEmployee(EmployeeInsertDTO employeeInsertDTO) {
        Employee employee = employeeMapper.insertDtoToEmployee(employeeInsertDTO);

        Optional<Position> optionalPosition = positionRepository.findById(employeeInsertDTO.getPositionId());
        if (optionalPosition.isEmpty()) {
            return Result.error("Position provided not found");

        }
        employee.setPosition(optionalPosition.get());

        employeeRepository.saveAndFlush(employee);

        // Assign email and phone in a second task
        companyHelper.assignEmailAndPhoneAsync(employee);

        return Result.success();
    }

    @Override
    @Transactional
    @Cacheable(value = "employeesByPage", key = "#pageable.pageNumber + '-' + #pageable.pageSize + '-' + #pageable.sort.toString()")
    public Page<EmployeeDTO> getEmployeesByPagesSortedByName(Pageable pageable) {
            Page<Employee> employeePage = employeeRepository.findAllByOrderByLastName(pageable);
            return employeePage.map(employeeMapper::employeeToDTO);
    }

    @Override
    @Transactional
    @Cacheable(value = "employeeById", key = "#employeeId")
    public Result<EmployeeDTO> getEmployeeById(Long employeeId) {
            Optional<Employee> optionalEmployee = employeeRepository.findById(employeeId);
            return optionalEmployee.map(employee ->  Result.success(employeeMapper.employeeToDTO(employee)))
                    .orElseGet(() -> Result.error("employee not found") );
    }

    @Override
    public Result<EmployeeDTO> getEmployeeByEmailOrPhoneOrID(RequestEmployeeUser requestEmployeeUser) {
        Optional<Employee> optionalEmployee = Optional.empty();

        if (requestEmployeeUser.getId() != null) {
            optionalEmployee = employeeRepository.findById(requestEmployeeUser.getId());
        } else if (requestEmployeeUser.getCompanyEmail() != null) {
            optionalEmployee = employeeRepository.findByCompanyEmail(requestEmployeeUser.getCompanyEmail());
        } else if (requestEmployeeUser.getCompanyPhone() != null) {
            optionalEmployee = employeeRepository.findByCompanyPhone(requestEmployeeUser.getCompanyPhone());
        }

        if (optionalEmployee.isEmpty()) {
            return Result.error("Employee not found with provided information");
        }

        EmployeeDTO employeeDTO = optionalEmployee.map(employeeMapper::employeeToDTO).get();
        return Result.success(employeeDTO);
    }

    @Override
    @Transactional
    public Result<Void> updateEmployee(EmployeeUpdateDTO employeeUpdateDTO) {
        Optional<Employee> optionalEmployee = employeeRepository.findById(employeeUpdateDTO.getEmployeeId());
        return optionalEmployee.map(employee -> {
            employeeMapper.updateEntity(employee, employeeUpdateDTO);
            employeeRepository.saveAndFlush(employee);

            return Result.success();
        }).orElseGet(() -> Result.error("employee not found"));
    }

    @Override
    @Transactional
    public Result<Void> deleteEmployee(Long employeeId) {
        if (!employeeRepository.existsById(employeeId)) {
            return Result.error("employee");
        }

        employeeRepository.deleteById(employeeId);
        return Result.success();
    }

    @Override
    @Cacheable(value = "validateExisitingEmployee", key = "#employeeId")
    public boolean validateExisitingEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId).isPresent();
    }
}
