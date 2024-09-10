package microservice.user_service.Service;


import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.LoginDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Cart.CartFacadeService;
import at.backend.drugstore.microservice.common_classes.GlobalFacadeService.Client.ClientFacadeService;
import at.backend.drugstore.microservice.common_classes.Utils.Result;
import microservice.user_service.Mappers.UserMapper;
import microservice.user_service.Model.User;
import microservice.user_service.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

@Service
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final AuthDomainService authDomainService;

    @Autowired
    public AuthServiceImpl(UserRepository userRepository,
                           UserMapper userMapper,
                           AuthDomainService authDomainService) {
        this.userRepository = userRepository;
        this.userMapper = userMapper;
        this.authDomainService = authDomainService;
    }

    @Override
    @Cacheable(value = "findUserCache", key = "#clientLoginDTO.email != null ? #clientLoginDTO.email : #clientLoginDTO.phoneNumber")
    public Result<UserLoginDTO> findUser(LoginDTO clientLoginDTO) {
        Optional<User> userOptional = Optional.empty();

        if (clientLoginDTO.getEmail() != null) {
            userOptional = userRepository.findByEmail(clientLoginDTO.getEmail());

            UserLoginDTO userToLoginDTO = userMapper.entityToLoginDTO(userOptional.get());
            return Result.success(userToLoginDTO);

        } else if (clientLoginDTO.getPhoneNumber() != null) {
            userOptional = userRepository.findByPhoneNumber(clientLoginDTO.getPhoneNumber());

            UserLoginDTO userToLoginDTO = userMapper.entityToLoginDTO(userOptional.get());
            return Result.success(userToLoginDTO);
        } else  {
            return Result.error("User not found with given credentials");
        }
    }

    @Override
    @Cacheable(value = "validateLoginCache", key = "#plainPassword.concat('-').concat(#hashPassword)")
    public Result<Void> validateLogin(String plainPassword, String hashPassword) {
      boolean isPasswordCorrect =  authDomainService.checkPassword(plainPassword, hashPassword);
         return isPasswordCorrect ? Result.success() : Result.error("Incorrect Password");

    }

    @Override
    @Async("taskExecutor")
    @CacheEvict(value = "userLoginCache", key = "#userDTO.email ?: #userDTO.phoneNumber")
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
