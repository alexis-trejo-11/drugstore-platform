package user_service.modules.users.infrastructure.adapter;

import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import user_service.modules.users.core.domain.models.entities.User;
import user_service.modules.users.core.ports.output.UserRepository;

@Service
public class CustomerUserDetailService implements UserDetailsService {
    private final UserRepository userRepository;

    @Autowired
    public CustomerUserDetailService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        try {
            UUID userId = UUID.fromString(username);
            User customUser = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(customUser.getEmail())
                    .password(customUser.getHashedPassword())
                    .roles(customUser.getRole().name())
                    .build();
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid UUID format: " + username);
        }

    }

}
