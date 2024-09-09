package microservice.user_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientLoginDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;

import at.backend.drugstore.microservice.common_classes.Utils.Result;
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
@RequestMapping("/v1/drugstore")
public class ClientAuthController {

    private final AuthService authService;

    @Autowired
    public ClientAuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/client-signup")
    @Operation(summary = "Sign up a new client", description = "Register a new client with the provided details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "User Created Successfully", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "400", description = "Bad Request", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public ResponseEntity<ResponseWrapper<String>> signUp(@RequestBody ClientSignUpDTO clientSignUpDTO) {
        log.info("Received signup request for client: {}", clientSignUpDTO);

        Result<Void> validationResult = authService.validateUniqueFields(clientSignUpDTO);
        if (!validationResult.isSuccess()) {
            log.warn("Signup unique field validation failed: {}", validationResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>(false, null, validationResult.getErrorMessage(), 400));
        }

        String jwtToken = authService.processSignup(clientSignUpDTO);
        log.info("Client signup successful for: {}", clientSignUpDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created("User"));
    }


    @PostMapping("/client-login")
    @Operation(summary = "Login a client", description = "Authenticate a client with the provided login details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Logged In", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public ResponseEntity<ResponseWrapper<String>> login(@Valid @RequestBody ClientLoginDTO clientLoginDTO) {
        log.info("Received login request for client: {}", clientLoginDTO.getEmail());

        Result<UserLoginDTO> findingResult = authService.findUser(clientLoginDTO);
        if (!findingResult.isSuccess()) {
            log.warn("Login failed: user not found for email: {}", clientLoginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(findingResult.getErrorMessage(), 404));
        }

        UserLoginDTO userDTO = findingResult.getData();

        Result<Void> validationResult = authService.validateLogin(clientLoginDTO.getPassword(), userDTO.getPassword());
        if (validationResult.isSuccess()) {
            log.warn("Login failed: incorrect validation for email: {}", clientLoginDTO.getEmail());
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.unauthorized(findingResult.getErrorMessage()));
        }


        CompletableFuture<String> processLoginFuture = authService.processLogin(userDTO);
        String JWT_TOKEN = processLoginFuture.join();
        return ResponseEntity.ok(ResponseWrapper.ok("User" ,"Authorize", JWT_TOKEN));
    }
}