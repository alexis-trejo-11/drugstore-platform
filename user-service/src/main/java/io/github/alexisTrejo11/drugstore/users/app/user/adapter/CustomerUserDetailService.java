package io.github.alexisTrejo11.drugstore.users.app.user.adapter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.entities.User;
import io.github.alexisTrejo11.drugstore.users.app.user.core.domain.models.valueobjects.UserId;
import io.github.alexisTrejo11.drugstore.users.app.user.core.ports.output.UserRepository;

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
            UserId userId = new UserId(username);
            User customUser = userRepository.findById(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));

            return org.springframework.security.core.userdetails.User.builder()
                    .username(customUser.getEmail() != null ? customUser.getEmail().value()
                            : customUser.getId().toString())
                    .password(customUser.getHashedPassword())
                    .roles(customUser.getRole().name())
                    .build();
        } catch (IllegalArgumentException e) {
            throw new UsernameNotFoundException("Invalid UUID format: " + username);
        }

    }

}
