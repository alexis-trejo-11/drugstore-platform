package microservice.user_service.Controller;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientLoginDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.UserDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_models.Utils.ErrorResponseUtil;
import at.backend.drugstore.microservice.common_models.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.user_service.Model.User;
import microservice.user_service.Service.AuthService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@RestController
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
     * @return A CompletableFuture with a ResponseEntity containing the result of the signup process
     */
    @PostMapping("/signup/clients")
    public CompletableFuture<ResponseEntity<ResponseWrapper<String>>> signUp(@Valid @RequestBody ClientSignUpDTO clientSignUpDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = new HashMap<>();
            bindingResult.getFieldErrors().forEach(error ->
                    errors.put(error.getField(), error.getDefaultMessage())
            );
            ResponseWrapper validationErrorResponse = new ResponseWrapper<>(errors, "validation error");
            return CompletableFuture.completedFuture(new ResponseEntity<>(validationErrorResponse, HttpStatus.BAD_REQUEST));
        }

        // Validate unique fields and create client using Client-Service
        CompletableFuture<String> validationFuture = authService.ValidateUniqueFields(clientSignUpDTO);
        CompletableFuture<Result<ClientDTO>> createClientFuture = authService.createClient(clientSignUpDTO);

        // Combine the results of validation and client creation, then process the result asynchronously
        return CompletableFuture.allOf(validationFuture, createClientFuture).thenComposeAsync(Void -> {
            String validationMessage = validationFuture.join();
            if (validationMessage != null) {
                ResponseWrapper<String> validationErrorResponse = new ResponseWrapper<>(null, validationMessage, HttpStatus.CONFLICT);
                return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.CONFLICT).body(validationErrorResponse));
            }

            Result<ClientDTO> clientDTOResult = createClientFuture.join();
            if (!clientDTOResult.isSuccess()) {
                ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, clientDTOResult.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
            }

            // Create client cart
            return authService.createClientCart(clientDTOResult.getData().getId()).thenCompose(cartResult -> {
                if (!cartResult.isSuccess()) {
                    ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, cartResult.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                    return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse));
                }

                // Create user and return JWT token
                CompletableFuture<String> createUserFuture = authService.createUser(clientSignUpDTO, clientDTOResult.getData());
                return createUserFuture.thenApply(token -> {
                    ResponseWrapper<String> response = new ResponseWrapper<>(token, null, HttpStatus.CREATED);
                    return ResponseEntity.status(HttpStatus.CREATED).body(response);
                });
            });
        }).exceptionally(ex -> {
            // Handle any exceptions that occur during the signup process
            logger.error("Error occurred during signup process", ex);
            ResponseWrapper<String> response = new ResponseWrapper<>(null, ex.getMessage(), HttpStatus.CREATED);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        });
    }

    /**
     * Endpoint for user login.
     *
     * @param clientLoginDTO The login credentials.
     * @param bindingResult  The binding result for validation.
     * @return A CompletableFuture containing the ResponseEntity with the JWT token or error message.
     */
    @PostMapping("/login")
    public CompletableFuture<ResponseEntity<ResponseWrapper<String>>> login(@Valid @RequestBody ClientLoginDTO clientLoginDTO, BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            Map<String, String> errors = ErrorResponseUtil.getErrorMessages(bindingResult);
            ResponseWrapper validationErrorResponse = new ResponseWrapper<>(errors, "Validation error");
            return CompletableFuture.completedFuture(ResponseEntity.badRequest().body(validationErrorResponse));
        }

        // Find user by email or phone number
        return authService.findUser(clientLoginDTO).thenCompose(result -> {
            if (!result.isSuccess()) {
                ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, result.getErrorMessage(), HttpStatus.NOT_FOUND);
                return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse));
            }

            UserLoginDTO userDTO = result.getData();

            // Check password asynchronously
            return authService.checkPassword(clientLoginDTO.getPassword(), userDTO.getHashedPassword())
                    .thenCompose(passwordCheckResult -> {
                        if (!passwordCheckResult.isSuccess()) {
                            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, passwordCheckResult.getErrorMessage(), HttpStatus.UNAUTHORIZED);
                            return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse));
                        }

                        // Process login to update last login timestamp and generate JWT token
                        return authService.processLogin(userDTO).thenApply(loginResult -> {
                            if (loginResult == null) {
                                ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, "Error occurred during login processing", HttpStatus.INTERNAL_SERVER_ERROR);
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                            } else if (!loginResult.isSuccess()) {
                                ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, loginResult.getErrorMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
                                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
                            } else {
                                ResponseWrapper<String> response = new ResponseWrapper<>(loginResult.getData(), null, HttpStatus.ACCEPTED);
                                return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
                            }
                        });
                    });
        }).exceptionally(ex -> {
            ResponseWrapper<String> errorResponse = new ResponseWrapper<>(null, "Internal server error: " + ex.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        });

    }

    }