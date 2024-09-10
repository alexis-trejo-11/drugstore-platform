package microservice.user_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Employee.EmployeeFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.user_service.Repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class EmployeeAuthServiceImpl implements EmployeeAuthService {
    private final EmployeeFacadeService employeeFacadeService;
    private final AuthDomainService authDomainService;
    private final UserRepository userRepository;

    public EmployeeAuthServiceImpl(EmployeeFacadeService employeeFacadeService, AuthDomainService authDomainService, UserRepository userRepository) {
        this.employeeFacadeService = employeeFacadeService;
        this.authDomainService = authDomainService;
        this.userRepository = userRepository;
    }


    @Override
    public Result<EmployeeDTO> validateExistingEmployee(RequestEmployeeUser requestEmployeeUser) {
        if (requestEmployeeUser.getCompanyEmail() == null && requestEmployeeUser.getCompanyPhone() == null && requestEmployeeUser.getId() == null) {
            return Result.error("No values found for search employee.");
        }

        // Get EmployeeDTO looking ib Employee Service
        CompletableFuture<Result<EmployeeDTO>> resultCompletableFuture = employeeFacadeService.getEmployeeForUserCreation(requestEmployeeUser);
        Result<EmployeeDTO> employeeDTOResult = resultCompletableFuture.join();

        if (!employeeDTOResult.isSuccess()) {
            return  Result.error(employeeDTOResult.getErrorMessage());
        }

        return Result.success(employeeDTOResult.getData());
    }

    @Override
    public boolean validateUniqueAccountPerEmployee(Long employeeId) {
        return userRepository.findByEmployeeId(employeeId).isPresent();
    }

    @Override
    public String processEmployeeSignup(String password, EmployeeDTO employeeDTO) {
       var userCreationFuture =  authDomainService.processEmployeeUserCreation(password, employeeDTO);
       return  userCreationFuture.join();
    }
}
