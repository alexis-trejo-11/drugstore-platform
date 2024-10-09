package microservice.employee_service.Utils;

import microservice.employee_service.Model.Employee;
import microservice.employee_service.Model.PhoneNumber;
import microservice.employee_service.Repository.EmployeeRepository;
import microservice.employee_service.Repository.PhoneRepository;
import microservice.employee_service.Service.EmployeeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Component
public class CompanyHelper {

    private static final String COMPANY_DOMINION = "@atcompany.com.mx";

    private final PhoneRepository phoneRepository;
    private final EmployeeRepository employeeRepository;

    @Autowired
    public CompanyHelper(PhoneRepository phoneRepository, EmployeeRepository employeeRepository) {
        this.phoneRepository = phoneRepository;
        this.employeeRepository = employeeRepository;
    }

    @Async("taskExecutor")
    public void assignEmailAndPhoneAsync(Employee employee) {
         String companyEmail = companyEmailGenerator(employee.getFirstName(), employee.getLastName(), employee.getId());
         employee.setCompanyEmail(companyEmail);

         String companyPhone = getAndAssignCompanyPhone(employee);
         if (companyPhone != null) {
            employee.setCompanyPhone(companyPhone);
         }

         employeeRepository.saveAndFlush(employee);
    }

    private String companyEmailGenerator(String firstName, String lastName, Long employeeId) {
        String name = firstName.split(" ")[0].toLowerCase();
        String lastname = lastName.split(" ")[0].toLowerCase();
        return name + "." + lastname + employeeId.toString() + COMPANY_DOMINION;
    }

    private String getAndAssignCompanyPhone(Employee employee) {
        Optional<PhoneNumber> phoneNumberOptional = phoneRepository.findFirstByEmployeeIsNullNative();

        if (phoneNumberOptional.isPresent()) {
            PhoneNumber phoneNumber = phoneNumberOptional.get();
            phoneNumber.setEmployee(employee);
            phoneNumber.setAssignedAt(LocalDateTime.now());
            phoneRepository.saveAndFlush(phoneNumber);
            return phoneNumber.getNumber();
        } else {
            return null;
        }
    }
}
