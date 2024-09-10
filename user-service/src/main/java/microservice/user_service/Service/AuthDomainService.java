package microservice.user_service.Service;

import at.backend.drugstore.microservice.common_classes.DTOs.Client.ClientDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.Employee.EmployeeDTO;
import at.backend.drugstore.microservice.common_classes.DTOs.User.ClientSignUpDTO;
import at.backend.drugstore.microservice.common_classes.Security.AuthSecurity;
import lombok.extern.slf4j.Slf4j;
import microservice.user_service.Mappers.UserMapper;
import microservice.user_service.Middleware.PasswordUtil;
import microservice.user_service.Model.Role;
import microservice.user_service.Model.User;
import microservice.user_service.Repository.RoleRepository;
import microservice.user_service.Repository.UserRepository;
import org.checkerframework.checker.index.qual.SameLen;
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

@Slf4j
@Component
public class AuthDomainService {

    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final AuthSecurity authSecurity;


    @Autowired
    public AuthDomainService(UserMapper userMapper,
                             RoleRepository roleRepository,
                             UserRepository userRepository,
                             AuthSecurity authSecurity) {
        this.userMapper = userMapper;
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.authSecurity = authSecurity;
    }

    @Transactional
    public String processClientUserCreation(ClientSignUpDTO clientSignUpDTO, ClientDTO clientDTO) {
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
    }

    @Transactional
    @Async("taskExecutor")
    public CompletableFuture<String> processEmployeeUserCreation(String password, EmployeeDTO employeeDTO) {
        // Map employee DTO to User entity Async
        CompletableFuture<User> userFuture =  CompletableFuture.supplyAsync(() -> userMapper.employeeDtoToEntity(employeeDTO));

        // Hash the password asynchronously
        CompletableFuture<String> hashedPasswordFuture = CompletableFuture.supplyAsync(() ->
                PasswordUtil.hashPassword(password)
        );

        // Find roles for the employee asynchronously
        CompletableFuture<Optional<Role>> positionRoleFuture = CompletableFuture.supplyAsync(() ->
                roleRepository.findByName(employeeDTO.getPosition())
        );

        CompletableFuture<Optional<Role>> commonEmployeeRoleFuture = CompletableFuture.supplyAsync(() ->
                roleRepository.findByName("employee")
        );


        // Combine all async operations
        return CompletableFuture.allOf(hashedPasswordFuture, positionRoleFuture, commonEmployeeRoleFuture, userFuture)
                .thenApplyAsync(voidResult -> {
                    // Get the hashed password
                    String hashedPassword = hashedPasswordFuture.join();

                    User user = userFuture.join();
                    user.setPassword(hashedPassword);

                    // Handle roles
                    Optional<Role> positionRoleOptional = positionRoleFuture.join();
                    Optional<Role> commonEmployeeRoleOptional = commonEmployeeRoleFuture.join();

                    // Ensure both roles exist or create them
                    Role positionRole = positionRoleOptional.orElseGet(() -> createNewRole(employeeDTO.getPosition()));
                    Role commonEmployeeRole = commonEmployeeRoleOptional.orElseGet(() -> createNewRole("employee"));

                    // Assign roles to the user
                    List<Role> roles = List.of(positionRole, commonEmployeeRole);
                    user.setRoles(roles);

                    log.info("processEmployeeUserCreation --> {}", user);
                    // Save the user
                    userRepository.saveAndFlush(user);

                    // Generate JWT token for the employee
                    return generateJwtToken(user, employeeDTO);
                });
    }

    private Role createNewRole(String roleName) {
        Role role = new Role();
        role.setName(roleName);
        return roleRepository.save(role);
    }


    private String generateJwtToken(User user, EmployeeDTO employeeDTO) {
        List<String> rolesToString = user.getRoles().stream()
                .map(Role::getName)
                .collect(Collectors.toList());

        // Return the generated JWT token
        return authSecurity.generateToken(user.getId(), rolesToString, employeeDTO.getId());
    }


    public Boolean checkPassword(String plainPassword, String hashPassword ) {
         return PasswordUtil.validatePassword(plainPassword, hashPassword);

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
