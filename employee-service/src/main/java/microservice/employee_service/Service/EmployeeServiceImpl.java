package microservice.employee_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeUpdateDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
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

    @Autowired
    public EmployeeServiceImpl(EmployeeRepository employeeRepository,
                               CompanyHelper companyHelper,
                               EmployeeMapper employeeMapper) {
        this.employeeRepository = employeeRepository;
        this.companyHelper = companyHelper;
        this.employeeMapper = employeeMapper;
    }

    @Override
    @Transactional
    public void addEmployee(EmployeeInsertDTO employeeDTO) {
        Employee employee = new Employee(employeeDTO);

        employeeRepository.saveAndFlush(employee);

        String employeeEmail = companyHelper.companyEmailGenerator(employee.getFirstName(), employee.getLastName(), employee.getId());
        employee.setCompanyEmail(employeeEmail);

        String phoneNumber = companyHelper.getAndAssignCompanyPhone(employee);
        if (phoneNumber != null) {
            employee.setCompanyPhone(phoneNumber);
        }

        employeeRepository.saveAndFlush(employee);
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
    public EmployeeDTO getEmployeeById(Long employeeId) {
            Employee employee = employeeRepository.findById(employeeId).orElse(null);
            return employeeMapper.employeeToDTO(employee);
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
    public void updateEmployee(EmployeeUpdateDTO employeeUpdateDTO) {
            Employee employee = employeeRepository.findById(employeeUpdateDTO.getId()).orElse(null);
            if (employee == null) {return;}

            employeeMapper.updateEntity(employee, employeeUpdateDTO);

            employeeRepository.saveAndFlush(employee);
    }

    @Override
    @Transactional
    public void deleteEmployee(Long employeeId) {
        employeeRepository.deleteById(employeeId);
    }

    @Override
    @Cacheable(value = "validateExisitingEmployee", key = "#employeeId")
    public boolean validateExisitingEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId).isPresent();
    }

}
