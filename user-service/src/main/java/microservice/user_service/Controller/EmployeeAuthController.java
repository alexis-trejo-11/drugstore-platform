package microservice.user_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import jakarta.validation.Valid;
import microservice.user_service.Service.AuthService;
import microservice.user_service.Service.EmployeeAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("v1/drugstore/auth")
public class EmployeeAuthController {

    private final EmployeeAuthService employeeAuthService;

    @Autowired
    public EmployeeAuthController(AuthService authService, EmployeeAuthService employeeAuthService) {
        this.employeeAuthService = employeeAuthService;
    }

    @PostMapping("/employee-signup")
    public ResponseEntity<ResponseWrapper<String>> employeeSignup(@Valid @RequestBody RequestEmployeeUser requestEmployeeUser) {
        // For Employee-User Creation an Existing Employee must be on Database
        Result<EmployeeDTO> employeeDTOResult = employeeAuthService.validateExistingEmployee(requestEmployeeUser);
        if (!employeeDTOResult.isSuccess()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ResponseWrapper.badRequest(employeeDTOResult.getErrorMessage()));
        }

        boolean employeeHasAccount = employeeAuthService.validateUniqueAccountPerEmployee(employeeDTOResult.getData().getId());
        if (employeeHasAccount) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(ResponseWrapper.error("Employee already has an account.", 409));
        }

        String jwtToken = employeeAuthService.processEmployeeSignup(requestEmployeeUser.getPassword(), employeeDTOResult.getData());

        return ResponseEntity.ok(ResponseWrapper.ok("User-Employee", "Create", jwtToken));
    }
}
