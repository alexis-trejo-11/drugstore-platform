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
import microservice.user_service.Mappers.UserMapper;
import microservice.user_service.Middleware.JwtUtil;
import microservice.user_service.Model.Role;
import microservice.user_service.Model.User;
import microservice.user_service.Repository.RoleRepository;
import microservice.user_service.Repository.UserRepository;
import microservice.user_service.Middleware.PasswordUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;


@Service
public class AuthService {

    private final ExternalCartService externalCartService;
    private final UserRepository userRepository;
    private final JwtUtil jwtUtil;
    private final ExternalClientService externalClientService;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;

    @Autowired
    public AuthService(ExternalCartService externalCartService, UserRepository userRepository, JwtUtil jwtUtil, ExternalClientServiceImpl externalClientService, RoleRepository roleRepository, UserMapper userMapper) {
        this.externalCartService = externalCartService;
        this.userRepository = userRepository;
        this.jwtUtil = jwtUtil;
        this.externalClientService = externalClientService;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
    }

    @Async
    public Result<Void> ValidateUniqueFields(ClientSignUpDTO clientSignUpDTO) {
        if (clientSignUpDTO.getEmail() != null) {
            Optional<User> userEmailOptional = userRepository.findByEmail(clientSignUpDTO.getEmail());
            if (userEmailOptional.isPresent()) {
                return Result.error("Email Already Taken");
            }
        }

        if (clientSignUpDTO.getPhoneNumber() != null) {
            Optional<User> userPhoneOptional = userRepository.findByEmail(clientSignUpDTO.getPhoneNumber());
            if (userPhoneOptional.isPresent()) {
                return Result.error("Phone Number Already Taken");
            }
        }
        return Result.success();
    }

    @Async
    @Transactional
    public String processSignup(ClientSignUpDTO clientSignUpDTO) {
        ClientDTO clientDTO = createClient(clientSignUpDTO);

        externalCartService.createClientCart(clientDTO.getId());

        return createUser(clientSignUpDTO, clientDTO);
    }

    private ClientDTO createClient(ClientSignUpDTO clientSignUpDTO) {
        ClientInsertDTO clientInsertDTO = userMapper.clientSignupDtoToClientInsertDTO(clientSignUpDTO);
        Result<ClientDTO> clientDTOResult = externalClientService.createClient(clientInsertDTO);
        return clientDTOResult.getData();
    }


    @Async
    @Transactional
    private String createUser(ClientSignUpDTO clientSignUpDTO, ClientDTO clientDTO) {
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
    }

    @Async
    public Result<UserLoginDTO> findUser(ClientLoginDTO clientLoginDTO) {
        Optional<User> userOptional = Optional.empty();

        if (clientLoginDTO.getEmail() != null) {
            userOptional = userRepository.findByEmail(clientLoginDTO.getEmail());
        } else if (clientLoginDTO.getPhoneNumber() != null) {
            userOptional = userRepository.findByPhoneNumber(clientLoginDTO.getPhoneNumber());
        }

        if (userOptional.isPresent()) {
            UserLoginDTO userToLoginDTO = userMapper.entityToLoginDTO(userOptional.get());

            return Result.success(userToLoginDTO);
        }
        return Result.error("User not found with given credentials");
    }

    @Async
    public Result<Void> checkPassword(String plainPassword, String hashPassword) {
        boolean isPasswordCorrect = PasswordUtil.validatePassword(plainPassword, hashPassword);
        if(!isPasswordCorrect) {
            return Result.error("Wrong Password, Try Again.");
        }
        return Result.success();
    }

    @Async
    @Transactional
    public String processLogin(UserLoginDTO userDTO) {
            Optional<User> userOptional = userRepository.findById(userDTO.getId());
            if (userOptional.isEmpty()) {
                return "";
            }

        User user = userOptional.get();

        userOptional.get().setLastLogin(LocalDateTime.now());
        userRepository.saveAndFlush(user);

        // Create JWToken
        List<String> rolesToString = new ArrayList<>();
        for (var role : user.getRoles()) {
            rolesToString.add(role.getName());
        }

        return jwtUtil.GenerateToken(user.getId(), rolesToString);
    }
}

