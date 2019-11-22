package net.explorviz.eaas.security;

import net.explorviz.eaas.model.User;
import net.explorviz.eaas.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

/**
 * Implements a {@link UserDetailsService} for Spring's built-in authentication, backed by our {@link User} database.
 */
public class UserService implements UserDetailsService, UserDetailsPasswordService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        User user = ((User) loadUserByUsername(userDetails.getUsername()));
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Unknown username"));
    }
}
