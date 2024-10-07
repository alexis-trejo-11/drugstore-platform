package microservice.user_service.Controller;

import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.Utils.ResponseWrapper;

import at.backend.drugstore.microservice.common_classes.Utils.Result;
import lombok.extern.slf4j.Slf4j;
import microservice.user_service.Service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import microservice.user_service.Service.ClientAuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/v1/drugstore/auth")
public class ClientAuthController {

    private final ClientAuthService clientAuthService;

    @Autowired
    public ClientAuthController(ClientAuthService clientAuthService) {
        this.clientAuthService = clientAuthService;
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

        Result<Void> validationResult = clientAuthService.validateUniqueFields(clientSignUpDTO);
        if (!validationResult.isSuccess()) {
            log.warn("Signup unique field validation failed: {}", validationResult.getErrorMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new ResponseWrapper<>(false, null, validationResult.getErrorMessage(), 400));
        }

        String jwtToken = clientAuthService.processClientSignup(clientSignUpDTO);
        log.info("Client signup successful for: {}", clientSignUpDTO.getEmail());
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseWrapper.created(jwtToken,"User"));
    }
}