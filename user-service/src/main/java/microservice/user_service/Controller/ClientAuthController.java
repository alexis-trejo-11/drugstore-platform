package microservice.user_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientLoginDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;

import lombok.extern.slf4j.Slf4j;
import microservice.user_service.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/v1/api")
public class ClientAuthController {

    private final AuthService authService;

    @Autowired
    public ClientAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/signup/clients")
    @Operation(summary = "Sign up a new client", description = "Register a new client with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User Created Successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public CompletableFuture<ResponseEntity<ResponseWrapper<String>>> signUp(
             @RequestBody ClientSignUpDTO clientSignUpDTO) {
        log.info("Received signup request for client: {}", clientSignUpDTO);

        return authService.validateUniqueFields(clientSignUpDTO).thenCompose(uniqueFieldsResult -> {
            if (!uniqueFieldsResult.isSuccess()) {
                log.warn("Signup unique field validation failed: {}", uniqueFieldsResult.getErrorMessage());
                return CompletableFuture.completedFuture(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>(false, null, uniqueFieldsResult.getErrorMessage(), 400)));
            }

            return authService.processSignup(clientSignUpDTO).thenApply(jwtToken -> {
                log.info("Client signup successful for: {}", clientSignUpDTO.getEmail());
                return ResponseEntity.status(HttpStatus.CREATED).body(new ResponseWrapper<>(true, jwtToken, "User Created Successfully.", 201));
            });
        });
    }


    @PostMapping("/login")
    @Operation(summary = "Login a client", description = "Authenticate a client with the provided login details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Logged In", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public CompletableFuture<ResponseEntity<ResponseWrapper<String>>> login(@Valid @RequestBody ClientLoginDTO clientLoginDTO) {
        log.info("Received login request for client: {}", clientLoginDTO.getEmail());
        return authService.findUser(clientLoginDTO)
                .thenCompose(userLoginDTOResult -> {
                    if (!userLoginDTOResult.isSuccess()) {
                        log.warn("Login failed: user not found for email: {}", clientLoginDTO.getEmail());
                        return CompletableFuture.completedFuture(
                                ResponseEntity.status(HttpStatus.NOT_FOUND)
                                        .body(new ResponseWrapper<>(false, null, userLoginDTOResult.getErrorMessage(), 404))
                        );
                    }

                    UserLoginDTO userDTO = userLoginDTOResult.getData();

                    return authService.validateLogin(clientLoginDTO.getPassword(), userDTO.getPassword())
                            .thenCompose(isUserValidated -> {
                                if (!isUserValidated.isSuccess()) {
                                    log.warn("Login failed: incorrect validation for email: {}", clientLoginDTO.getEmail());
                                    return CompletableFuture.completedFuture(
                                            ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                                                    .body(new ResponseWrapper<>(false, null, isUserValidated.getErrorMessage(), 401))
                                    );
                                }

                                return authService.processLogin(userDTO)
                                        .thenApply(jwtToken -> {
                                            log.info("Client login successful for: {}", clientLoginDTO.getEmail());
                                            return ResponseEntity.status(HttpStatus.OK)
                                                    .body(new ResponseWrapper<>(true, jwtToken, "Successfully Logged In.", 200));
                                        });
                            });
                });
    }
}
