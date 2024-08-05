package microservice.user_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.User.ClientLoginDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_models.Utils.ApiResponse;
import at.backend.drugstore.microservice.common_models.Validations.ControllerValidation;
import microservice.user_service.Service.AuthServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
@RequestMapping("/v1/api")
public class ClientAuthController {

    private final AuthServiceImpl authService;
    private final Logger logger = LoggerFactory.getLogger(ClientAuthController.class);

    @Autowired
    public ClientAuthController(AuthServiceImpl authService) {
        this.authService = authService;
    }

    @PostMapping("/signup/clients")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> signUp(@Valid @RequestBody ClientSignUpDTO clientSignUpDTO) {
        logger.info("Received signup request for client: {}", clientSignUpDTO.getEmail());

        return authService.validateUniqueFields(clientSignUpDTO).thenCompose(uniqueFieldsResult -> {
            if (!uniqueFieldsResult.isSuccess()) {
                logger.warn("Signup unique field validation failed: {}", uniqueFieldsResult.getErrorMessage());
                return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ApiResponse<>(false, null, uniqueFieldsResult.getErrorMessage(), 400)));
            }

            return authService.processSignup(clientSignUpDTO).thenApply(jwtToken -> {
                logger.info("Client signup successful for: {}", clientSignUpDTO.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse<>(true, jwtToken, "User Created Successfully.", 201));
            });
        });
    }

    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<ApiResponse<String>>> login(@Valid @RequestBody ClientLoginDTO clientLoginDTO) {
        logger.info("Received login request for client: {}", clientLoginDTO.getEmail());
        return authService.findUser(clientLoginDTO)
                .thenCompose(userLoginDTOResult -> {
                    if (!userLoginDTOResult.isSuccess()) {
                        logger.warn("Login failed: user not found for email: {}", clientLoginDTO.getEmail());
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ApiResponse<>(false, null, userLoginDTOResult.getErrorMessage(), 404))
                        );
                    }

                    UserLoginDTO userDTO = userLoginDTOResult.getData();

                    return authService.checkPassword(clientLoginDTO.getPassword(), userDTO.getPassword())
                            .thenCompose(passwordResult -> {
                                if (!passwordResult.isSuccess()) {
                                    logger.warn("Login failed: incorrect password for email: {}", clientLoginDTO.getEmail());
                                    return CompletableFuture.completedFuture(
                                            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                    .body(new ApiResponse<>(false, null, passwordResult.getErrorMessage(), 401))
                                    );
                                }

                                return authService.processLogin(userDTO)
                                        .thenApply(jwtToken -> {
                                            logger.info("Client login successful for: {}", clientLoginDTO.getEmail());
                                            return ResponseEntity.status(HttpStatus.OK)
                                                    .body(new ApiResponse<>(true, jwtToken, "Successfully Logged In.", 200));
                                        });
                            });
                });
    }
}
