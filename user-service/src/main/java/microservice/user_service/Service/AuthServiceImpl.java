package microservice.user_service.Service;

import at.backend.drugstore.microservice.common_models.DTO.Client.ClientDTO;
import at.backend.drugstore.microservice.common_models.DTO.Client.ClientInsertDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientLoginDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_models.DTO.User.UserLoginDTO;
import at.backend.drugstore.microservice.common_models.ExternalService.Cart.ExternalCartService;
import at.backend.drugstore.microservice.common_models.ExternalService.Client.ExternalClientService;
import at.backend.drugstore.microservice.common_models.Utils.Result;
import microservice.user_service.Mappers.UserMapper;
import microservice.user_service.Middleware.JwtUtil;
import microservice.user_service.Model.Role;
import microservice.user_service.Model.User;
import microservice.user_service.Repository.RoleRepository;
import microservice.user_service.Repository.UserRepository;
import microservice.user_service.Middleware.PasswordUtil;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
public class AuthServiceImpl implements AuthService {

    private final ExternalCartService externalCartService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ExternalClientService externalClientService;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Autowired
    public AuthServiceImpl(ExternalCartService externalCartService,
                           UserRepository userRepository,
                           JwtUtil jwtUtil,
                           ExternalClientService externalClientService,
                           RoleRepository roleRepository,
                           UserMapper userMapper) {
        this.externalCartService = externalCartService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.externalClientService = externalClientService;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
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
    @Transactional
    public CompletableFuture<String> processSignup(ClientSignUpDTO clientSignUpDTO) {
        ClientInsertDTO clientInsertDTO = userMapper.clientSignupDtoToClientInsertDTO(clientSignUpDTO);

        // Crear cliente de manera asíncrona
        CompletableFuture<ClientDTO> clientFuture = externalClientService.createClient(clientInsertDTO);

        // Crear carrito de cliente en paralelo con la creación del cliente
        CompletableFuture<Void> clientCartFuture = clientFuture.thenCompose(clientDTO ->
                externalCartService.createClientCart(clientDTO.getId())
        );

        // Procesar usuario una vez que el cliente se ha creado y el carrito se ha creado
        return clientFuture
                .thenCombine(clientCartFuture, (clientDTO, voidResult) -> clientDTO)
                .thenCompose(clientDTO -> processUser(clientSignUpDTO, clientDTO));
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
    public CompletableFuture<Result<Void>> checkPassword(String plainPassword, String hashPassword) {
        boolean isPasswordCorrect = PasswordUtil.validatePassword(plainPassword, hashPassword);
        return CompletableFuture.completedFuture(
                isPasswordCorrect ? Result.success() : Result.error("Wrong Password, Try Again.")
        );
    }



    @Override
    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<String> processLogin(UserLoginDTO userDTO) {
        CompletableFuture<Optional<User>> userFuture = CompletableFuture.supplyAsync(() ->
                userRepository.findById(userDTO.getId())
        );

        return userFuture.thenCompose(userOptional -> {
            if (userOptional.isEmpty()) {
                return CompletableFuture.completedFuture("");
            }

            User user = userOptional.get();
            Hibernate.initialize(user.getRoles());

            user.setLastLogin(LocalDateTime.now());
            return CompletableFuture.supplyAsync(() -> {
                userRepository.saveAndFlush(user);

                List<String> rolesToString = user.getRoles().stream()
                        .map(Role::getName)
                        .collect(Collectors.toList());

                return jwtUtil.GenerateToken(user.getId(), rolesToString);
            });
        });
    }


    @Async("taskExecutor")
    private CompletableFuture<String> processUser(ClientSignUpDTO clientSignUpDTO, ClientDTO clientDTO) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userMapper.signupDtoToEntity(clientSignUpDTO);
            user.setClientId(clientDTO.getId());

            String hashedPassword = PasswordUtil.hashPassword(clientSignUpDTO.getPassword());
            user.setPassword(hashedPassword);

            String jwtToken = "";

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

            userRepository.saveAndFlush(user);

            return jwtToken;
        });
    }
}
