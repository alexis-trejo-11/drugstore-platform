package microservice.user_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.Middleware.AuthSecurity;
import microservice.user_service.Mappers.UserMapper;
import microservice.user_service.Middleware.PasswordUtil;
import microservice.user_service.Model.Role;
import microservice.user_service.Model.User;
import microservice.user_service.Repository.RoleRepository;
import microservice.user_service.Repository.UserRepository;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Component
public class AuthDomainService {

    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthSecurity authSecurity;


    @Autowired
    public AuthDomainService(UserMapper userMapper,
                             RoleRepository roleRepository,
                             UserRepository userRepository, AuthSecurity authSecurity) {
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.authSecurity = authSecurity;
    }

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<String> processUserCreation(ClientSignUpDTO clientSignUpDTO, ClientDTO clientDTO) {
        return CompletableFuture.supplyAsync(() -> {
            User user = userMapper.signupDtoToEntity(clientSignUpDTO);
            user.setClientId(clientDTO.getId());

            String hashedPassword = PasswordUtil.hashPassword(clientSignUpDTO.getPassword());
            user.setPassword(hashedPassword);

            String jwtToken = "";

            Optional<Role> roleOptional = roleRepository.findByName("common_user");
            if (roleOptional.isPresent()) {
                Role role = roleOptional.get();

                role = roleRepository.findById(role.getId()).orElse(roleRepository.save(role));

                List<Role> roles = new ArrayList<>();
                roles.add(role);
                user.setRoles(roles);

                // Create JWT token
                List<String> rolesToString = new ArrayList<>();
                for (var roleItem : user.getRoles()) {
                    rolesToString.add(roleItem.getName());
                }

                jwtToken = authSecurity.generateToken(user.getId(), rolesToString, clientDTO.getId());
            }

            userRepository.saveAndFlush(user);

            return jwtToken;
        });
    }

    public CompletableFuture<Boolean> checkPassword(String plainPassword, String hashPassword ) {
        boolean isPasswordCorrect = PasswordUtil.validatePassword(plainPassword, hashPassword);
        return CompletableFuture.completedFuture(isPasswordCorrect);
    }

    @Async("taskExecutor")
    public CompletableFuture<String> generateJWToken(User user) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> rolesToString = user.getRoles().stream()
                    .map(Role::getName)
                    .collect(Collectors.toList());

            return authSecurity.generateToken(user.getId(), rolesToString, user.getClientId());
        });

    }

    @Async("taskExecutor")
    public CompletableFuture<Void> processUserAction(User user) {
        return CompletableFuture.runAsync(() ->  {
            Hibernate.initialize(user.getRoles());
            user.setLastLogin(LocalDateTime.now());
            userRepository.saveAndFlush(user);
        });
    }

}
