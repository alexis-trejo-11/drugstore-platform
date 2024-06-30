package microservice.employee_service.Utils;

import microservice.employee_service.Model.Employee;
import microservice.employee_service.Model.PhoneNumber;
import microservice.employee_service.Repository.PhoneRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Optional;

@Component
public class CompanyHelper {

    private static final String COMPANY_DOMINION = "@atcompany.com.mx";

    private final PhoneRepository phoneRepository;

    @Autowired
    public CompanyHelper(PhoneRepository phoneRepository) {
        this.phoneRepository = phoneRepository;
    }

    public String companyEmailGenerator(String firstName, String lastName) {
        String name = firstName.split(" ")[0].toLowerCase();
        String lastname = lastName.split(" ")[0].toLowerCase();
        return name + "." + lastname + COMPANY_DOMINION;
    }

    public String getAndAssignCompanyPhone(Employee employee) {
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
