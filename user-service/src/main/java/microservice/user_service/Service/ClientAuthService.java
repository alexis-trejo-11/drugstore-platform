package microservice.user_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.LoginDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.RequestEmployeeUser;
import at.backend.drugstore.microservice.common_classes.DTOs.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_classes.Utils.Result;

import java.util.concurrent.CompletableFuture;

public interface ClientAuthService {
    Result<Void> validateUniqueFields(ClientSignUpDTO clientSignUpDTO);
    String processClientSignup(ClientSignUpDTO clientSignUpDTO);
}
