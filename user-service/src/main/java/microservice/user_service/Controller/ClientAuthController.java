package microservice.user_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.User.ClientLoginDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.user_service.Service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/v1/api")
public class ClientAuthController {

    private final AuthService authService;
    private final Executor taskExecutor;
    private final Logger logger = LoggerFactory.getLogger(ClientAuthController.class);

    @Autowired
    public ClientAuthController(AuthService authService, Executor taskExecutor) {
        this.authService = authService;
        this.taskExecutor = taskExecutor;
    }

    /**
     * Handles the signup request for clients.
     *
     * @param clientSignUpDTO The DTO containing client signup data
     * @param bindingResult   The result of the data validation
     * @return A ResponseEntity containing the result of the signup process
     */
    @PostMapping("/signup/clients")
    public ResponseEntity<ApiResponse<?>> signUp(@Valid @RequestBody ClientSignUpDTO clientSignUpDTO, BindingResult bindingResult) {
        logger.info("Received signup request for client: {}", clientSignUpDTO.getEmail());

        if (bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            logger.warn("Signup validation errors: {}", validationErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationErrors, "Validation Errors", 400));
        }

        Result<Void> uniqueFieldsResult = authService.ValidateUniqueFields(clientSignUpDTO);
        if (!uniqueFieldsResult.isSuccess()) {
            logger.warn("Signup unique field validation failed: {}", uniqueFieldsResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, uniqueFieldsResult.getErrorMessage(), 400));
        }

        String jwtToken = authService.processSignup(clientSignUpDTO);
        logger.info("Client signup successful for: {}", clientSignUpDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, jwtToken, "User Created Successfully.", 201));
    }

    /**
     * Endpoint for user login.
     *
     * @param clientLoginDTO The login credentials.
     * @param bindingResult  The binding result for validation.
     * @return A ResponseEntity containing the JWT token or error message.
     */
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<?>> login(@Valid @RequestBody ClientLoginDTO clientLoginDTO, BindingResult bindingResult) {
        logger.info("Received login request for client: {}", clientLoginDTO.getEmail());

        if (bindingResult.hasErrors()) {
            var validationErrors = ControllerValidation.handleValidationError(bindingResult);
            logger.warn("Login validation errors: {}", validationErrors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, validationErrors, "Validation Errors", 400));
        }

        Result<UserLoginDTO> userLoginDTOResult = authService.findUser(clientLoginDTO);
        if (!userLoginDTOResult.isSuccess()) {
            logger.warn("Login failed: user not found for email: {}", clientLoginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ApiResponse<>(false, null, userLoginDTOResult.getErrorMessage(), 404));
        }

        UserLoginDTO userDTO = userLoginDTOResult.getData();

        Result<Void> passwordResult = authService.checkPassword(clientLoginDTO.getPassword(), userDTO.getPassword());
        if (!passwordResult.isSuccess()) {
            logger.warn("Login failed: incorrect password for email: {}", clientLoginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new ApiResponse<>(false, null, passwordResult.getErrorMessage(), 401));
        }

        String jwtToken = authService.processLogin(userDTO);
        logger.info("Client login successful for: {}", clientLoginDTO.getEmail());
        return ResponseEntity.status(HttpStatus.OK).body(new ApiResponse<>(true, jwtToken, "Successfully Logged In.", 200));
    }
}
