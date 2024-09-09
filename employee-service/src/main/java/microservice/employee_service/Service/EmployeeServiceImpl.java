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
import org.springframework.data.domain.Page;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;
import java.util.Optional;

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
    @Transactional
    public void addEmployee(EmployeeInsertDTO employeeDTO) {
        Employee employee = new Employee(employeeDTO);

        String employeeEmail = companyHelper.companyEmailGenerator(employee.getFirstName(), employee.getLastName());
        employee.setCompanyEmail(employeeEmail);

        employeeRepository.saveAndFlush(employee);

        String phoneNumber = companyHelper.getAndAssignCompanyPhone(employee);
        if (phoneNumber != null) {
            employee.setCompanyPhone(phoneNumber);
        }

        employeeRepository.saveAndFlush(employee);
    }

    @Override
    @Transactional
    public Page<EmployeeDTO> getEmployeesByPagesSortedByName(Pageable pageable) {
            Page<Employee> employeePage = employeeRepository.findAllByOrderByLastName(pageable);
            return employeePage.map(employeeMapper::employeeToDTO);
    }

    @Override
    @Transactional
    public EmployeeDTO getEmployeeById(Long id) {
            Employee employee = employeeRepository.findById(id).orElse(null);
            return employeeMapper.employeeToDTO(employee);
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
    public boolean validateExisitingEmployee(Long employeeId) {
        return employeeRepository.findById(employeeId).isPresent();
    }

}
