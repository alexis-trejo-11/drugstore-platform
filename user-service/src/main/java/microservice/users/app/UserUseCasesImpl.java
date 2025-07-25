package microservice.users.app;

import lombok.RequiredArgsConstructor;
import microservice.users.core.models.User;
import microservice.users.core.models.valueobjects.Email;
import microservice.users.core.models.valueobjects.Password;
import microservice.users.core.models.valueobjects.UserId;
import microservice.users.core.ports.input.UserUseCases;
import microservice.users.core.ports.output.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserUseCasesImpl implements UserUseCases {
    private final UserRepository userRepository;

    @Override
    public User createUser(User user) {
        if (userRepository.findByEmail(user.getEmail()).isPresent()) {
            throw new IllegalArgumentException("User with email already exists");
        }
        return userRepository.save(user);
    }

    @Override
    public Optional<User> getUserById(UserId id) {
        return userRepository.findById(id);
    }

    @Override
    public Optional<User> getUserByEmail(Email email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User updateUser(User user) {
        if (!userRepository.existsById(user.getId())) {
            throw new IllegalArgumentException("User not found");
        }
        return userRepository.save(user);
    }

    @Override
    public void deleteUser(UserId id) {
        if (!userRepository.existsById(id)) {
            throw new IllegalArgumentException("User not found");
        }
        userRepository.delete(id);
    }

    @Override
    public boolean changePassword(UserId id, Password oldPassword, Password newPassword) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isEmpty()) {
            return false;
        }
        
        User user = userOpt.get();
        if (!user.getPassword().equals(oldPassword)) {
            return false;
        }
        
        user.changePassword(newPassword);
        userRepository.save(user);
        return true;
    }

    @Override
    public boolean authenticateUser(Email email, Password password) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        return userOpt.isPresent() && userOpt.get().getPassword().equals(password);
    }
}
