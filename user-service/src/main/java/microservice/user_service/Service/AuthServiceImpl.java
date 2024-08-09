package microservice.user_service.Service;

import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTOs.User.ClientLoginDTO;
import at.backend.drugstore.microservice.common_models.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_models.DTOs.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Cart.CartFacadeService;
import at.backend.drugstore.microservice.common_models.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.user_service.Mappers.UserMapper;
import microservice.user_service.Model.User;
import microservice.user_service.Repository.UserRepository;
import microservice.user_service.Middleware.PasswordUtil;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthServiceImpl implements AuthService {

    private final CartFacadeService cartFacadeService;
    private final UserRepository userRepository;
    private final ClientFacadeService clientFacadeService;
    private final UserMapper userMapper;
    private final AuthDomainService authDomainService;

    @Autowired
    public AuthServiceImpl(CartFacadeService cartFacadeService,
                           UserRepository userRepository,
                           ClientFacadeService clientFacadeService,
                           UserMapper userMapper,
                           AuthDomainService authDomainService) {
        this.cartFacadeService = cartFacadeService;
        this.userRepository = userRepository;
        this.clientFacadeService = clientFacadeService;
        this.userMapper = userMapper;
        this.authDomainService = authDomainService;
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<Void>> validateUniqueFields(ClientSignUpDTO clientSignUpDTO) {
        if (clientSignUpDTO.getEmail() != null) {
            Optional<User> userEmailOptional = userRepository.findByEmail(clientSignUpDTO.getEmail());
            if (userEmailOptional.isPresent()) {
                return CompletableFuture.completedFuture(Result.error("Email Already Taken"));
            }
        }

        if (clientSignUpDTO.getPhoneNumber() != null) {
            Optional<User> userPhoneOptional = userRepository.findByPhoneNumber(clientSignUpDTO.getPhoneNumber());
            if (userPhoneOptional.isPresent()) {
                return CompletableFuture.completedFuture(Result.error("Phone Number Already Taken"));
            }
        }
        return CompletableFuture.completedFuture(Result.success());
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<String> processSignup(ClientSignUpDTO clientSignUpDTO) {
        ClientInsertDTO clientInsertDTO = userMapper.clientSignupDtoToClientInsertDTO(clientSignUpDTO);

        CompletableFuture<ClientDTO> clientFuture = clientFacadeService.createClient(clientInsertDTO);

        CompletableFuture<Void> clientCartFuture = clientFuture.thenCompose(clientDTO ->
                cartFacadeService.createClientCart(clientDTO.getId())
        );

        return clientFuture
                .thenCombine(clientCartFuture, (clientDTO, voidResult) -> clientDTO)
                .thenCompose(clientDTO -> authDomainService.processUserCreation(clientSignUpDTO, clientDTO));
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<Result<UserLoginDTO>> findUser(ClientLoginDTO clientLoginDTO) {
        Optional<User> userOptional = Optional.empty();

        if (clientLoginDTO.getEmail() != null) {
            userOptional = userRepository.findByEmail(clientLoginDTO.getEmail());
        } else if (clientLoginDTO.getPhoneNumber() != null) {
            userOptional = userRepository.findByPhoneNumber(clientLoginDTO.getPhoneNumber());
        }

        if (userOptional.isPresent()) {
            UserLoginDTO userToLoginDTO = userMapper.entityToLoginDTO(userOptional.get());

            return CompletableFuture.completedFuture(Result.success(userToLoginDTO));
        }
        return CompletableFuture.completedFuture(Result.error("User not found with given credentials"));
    }

    @Async("taskExecutor")
    public CompletableFuture<Result<Void>> validateLogin(String plainPassword, String hashPassword) {
    return authDomainService.checkPassword(plainPassword, hashPassword)
            .thenApply(isPasswordCorrect -> isPasswordCorrect ? Result.success() : Result.error("Incorrect Password"));
    }

    @Override
    @Async("taskExecutor")
    public CompletableFuture<String> processLogin(UserLoginDTO userDTO) {
        Optional<User> optionalUser = userRepository.findById(userDTO.getId());
        if (optionalUser.isEmpty()) {
            return CompletableFuture.completedFuture("");
        }

        User user = optionalUser.get();

        // Process User While JWT Token is getting generated
        CompletableFuture<Void> userProcessFuture = authDomainService.processUserAction(user);
        CompletableFuture<String> jwtFuture =  authDomainService.generateJWToken(user);

        // Combine both futures ensuring they both complete before returning the JWT future
        return userProcessFuture.thenCombine(jwtFuture, (voidResult, jwt) -> jwt);
    }
}
