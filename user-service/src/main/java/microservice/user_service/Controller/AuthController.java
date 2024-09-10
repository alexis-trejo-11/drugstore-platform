package microservice.user_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.User.LoginDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import microservice.user_service.Service.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/auth")
public class AuthController {

    private final AuthService authService;

    @Autowired
    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    @Operation(summary = "Login a client", description = "Authenticate a client with the provided login details")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successfully Logged In", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "401", description = "Unauthorized", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "404", description = "Not Found", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class))),
            @ApiResponse(responseCode = "500", description = "Internal Server Error", content = @Content(mediaType = "application/json", schema = @Schema(implementation = ResponseWrapper.class)))
    })
    public ResponseEntity<ResponseWrapper<String>> login(@Valid @RequestBody LoginDTO loginDTO) {
        log.info("Received login request for client: {}", loginDTO.getEmail());

        Result<UserLoginDTO> findingResult = authService.findUser(loginDTO);
        if (!findingResult.isSuccess()) {
            log.warn("Login failed: user not found for email: {}", loginDTO.getEmail());
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ResponseWrapper.error(findingResult.getErrorMessage(), 404));
        }

        UserLoginDTO userDTO = findingResult.getData();

        Result<Void> validationResult = authService.validateLogin(loginDTO.getPassword(), userDTO.getPassword());
        if (validationResult.isSuccess()) {
            log.warn("Login failed: incorrect validation for email: {}", loginDTO.getEmail());
            ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(ResponseWrapper.unauthorized(findingResult.getErrorMessage()));
        }


        CompletableFuture<String> processLoginFuture = authService.processLogin(userDTO);
        String JWT_TOKEN = processLoginFuture.join();
        return ResponseEntity.ok(ResponseWrapper.ok("User" ,"Authorize", JWT_TOKEN));
    }
}
