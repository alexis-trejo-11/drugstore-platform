package microservice.user_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientLoginDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Cart.ExternalCartService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientServiceImpl;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.user_service.Middleware.JwtUtil;
import microservice.user_service.Model.Role;
import microservice.user_service.Model.User;
import microservice.user_service.Repository.RoleRepository;
import microservice.user_service.Repository.UserRepository;
import microservice.user_service.Middleware.PasswordUtil;
import microservice.user_service.Utils.ModelTransformer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthService {

    private final ExternalCartService externalCartService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ExternalClientService externalClientService;
    private final RoleRepository roleRepository;
    private final Logger logger = LoggerFactory.getLogger(AuthService.class);

    @Autowired
    public AuthService(ExternalCartService externalCartService, UserRepository userRepository, JwtUtil jwtUtil, ExternalClientServiceImpl externalClientService, RoleRepository roleRepository) {
        this.externalCartService = externalCartService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.externalClientService = externalClientService;
        this.roleRepository = roleRepository;
    }

    @Async
    public CompletableFuture<String> ValidateUniqueFields(ClientSignUpDTO clientSignUpDTO) {
        try {
            if (clientSignUpDTO.getEmail() != null) {
                Optional<User> userEmailOptional = userRepository.findByEmail(clientSignUpDTO.getEmail());
                if (userEmailOptional.isPresent()) {
                    return CompletableFuture.completedFuture("Email Already Taken");
                }
            }

            if (clientSignUpDTO.getPhoneNumber() != null) {
                Optional<User> userPhoneOptional = userRepository.findByEmail(clientSignUpDTO.getPhoneNumber());
                if (userPhoneOptional.isPresent()) {
                    return CompletableFuture.completedFuture("Phone Number Already Taken");
                }

            }
            return CompletableFuture.completedFuture(null);
        } catch (Exception e) {
            logger.error("Error occurred while validating unique fields", e);
            return CompletableFuture.failedFuture(e);
        }

    }

    @Async
    @Transactional
    public CompletableFuture<Result<ClientDTO>> createClient(ClientSignUpDTO clientSignUpDTO) {
        try {
            // Transform clientSignUpDTO to clientInsertDTO
            ClientInsertDTO clientInsertDTO = ModelTransformer.ClientSignupDtoToClientInsertDTO(clientSignUpDTO);

            // Connect to Client Service
            CompletableFuture<Result<ClientDTO>> futureResponse = CompletableFuture.supplyAsync(() -> {
                ResponseEntity<Result<ClientDTO>> response = externalClientService.createClient(clientInsertDTO);

                if (response.getStatusCode() != HttpStatus.CREATED) {
                    if (response.getBody() != null) {
                        return Result.error(response.getBody().getErrorMessage());
                    } else {
                        return Result.error("Cannot create client: " + response.getStatusCode().getReasonPhrase());
                    }
                }

                if (response.getBody() != null && response.getBody().getData() != null) {
                    return Result.success(response.getBody().getData());
                } else {
                    return Result.error("Invalid response from client service");
                }
            });

            return futureResponse;
        } catch (Exception e) {
            logger.error("Error occurred while creating client", e);
            CompletableFuture<Result<ClientDTO>> futureError = new CompletableFuture<>();
            futureError.completeExceptionally(new RuntimeException("Internal server error"));
            return futureError;
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<String>> createClientCart(Long clientId) {
        try {
            // Connect To Cart Service
            ResponseEntity<Result<Void>> response = externalCartService.createClientCart(clientId);
            if (response.getStatusCode() != HttpStatus.CREATED) {
                return CompletableFuture.completedFuture(Result.error("Cant Create Cart"));
            }
            return CompletableFuture.completedFuture(Result.success("Cart Created"));
        } catch (Exception e) {
            logger.error("Error occurred while creating client", e);
            throw new RuntimeException(e);
        }

    }


    @Async
    @Transactional
    public CompletableFuture<String> createUser(ClientSignUpDTO clientSignUpDTO, ClientDTO clientDTO) {
        try {
            // Create User
            User user = ModelTransformer.ClientSignupDtoToUser(clientSignUpDTO);
            user.setClientId(clientDTO.getId());

            // Hash Password
            String hashedPassword = PasswordUtil.hashPassword(clientSignUpDTO.getPassword());
            user.setPassword(hashedPassword);

            String jwtToken = "";

            // Find Role and Append Common User Role
            Optional<Role> roleOptional = roleRepository.findByName("common_user");
            if (roleOptional.isPresent()) {
                List<Role> roles = new ArrayList<>();
                roles.add(roleOptional.get());
                user.setRoles(roles);

                // Create JWToken
                List<String> rolesToString = new ArrayList<>();
                for (var role : user.getRoles()) {
                    rolesToString.add(role.getName());
                }

                jwtToken = jwtUtil.GenerateToken(user.getId(), rolesToString);
            }

            // Insert user Into Database
            userRepository.saveAndFlush(user);

            return CompletableFuture.completedFuture(jwtToken);
        } catch (Exception e) {
            logger.error("Error occurred while creating user", e);
            throw new RuntimeException(e);
        }
    }

    @Async
    public CompletableFuture<Result<UserLoginDTO>> findUser(ClientLoginDTO clientLoginDTO) {
        try {
            Optional<User> userOptional = Optional.empty();

            if (clientLoginDTO.getEmail() != null) {
                userOptional = userRepository.findByEmail(clientLoginDTO.getEmail());
            } else if (clientLoginDTO.getPhoneNumber() != null) {
                userOptional = userRepository.findByPhoneNumber(clientLoginDTO.getPhoneNumber());
            }

            if (userOptional.isPresent()) {
                   UserLoginDTO userToLoginDTO = ModelTransformer.userToLoginDTO(userOptional.get());

                return CompletableFuture.completedFuture(Result.success(userToLoginDTO));
            } else {
                return CompletableFuture.completedFuture(Result.error("User not found with given credentials"));
            }

        } catch (Exception e) {
            logger.error("Error occurred while finding user", e);
            return CompletableFuture.failedFuture(e);
        }
    }

    @Async
    public CompletableFuture<Result<Void>> checkPassword(String plainPassword, String hashPassword) {
        try {
            boolean isPasswordCorrect = PasswordUtil.validatePassword(plainPassword, hashPassword);
            if(!isPasswordCorrect) {
                return CompletableFuture.completedFuture(Result.error("Wrong Password, Try Again."));
            }
            return CompletableFuture.completedFuture(Result.success());
        } catch (Exception e) {
            return CompletableFuture.failedFuture(new Throwable("Can Not Validate Password", e));
        }
    }

    @Async
    @Transactional
    public CompletableFuture<Result<String>> processLogin(UserLoginDTO userDTO) {
        try {
            Optional<User> userOptional = userRepository.findById(userDTO.getId());
            if (userOptional.isPresent()) {
                User user = userOptional.get();

                userOptional.get().setLastLogin(LocalDateTime.now());
                userRepository.saveAndFlush(user);

                // Create JWToken
                List<String> rolesToString = new ArrayList<>();
                for (var role : user.getRoles()) {
                    rolesToString.add(role.getName());
                }

                return CompletableFuture.completedFuture(Result.success(jwtUtil.GenerateToken(user.getId(), rolesToString)));
            } else {
                return CompletableFuture.completedFuture(null);

            }
        } catch (Exception e) {
            return CompletableFuture.failedFuture(e);
        }
    }
}
