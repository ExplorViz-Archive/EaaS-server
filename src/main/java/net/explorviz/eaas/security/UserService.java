package net.explorviz.eaas.security;

import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.UserRepository;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

/**
 * Implements a {@link UserDetailsService} for Spring's built-in authentication, backed by our {@link User} database.
 * <p>
 * Note: This {@link UserDetailsService} implementation will be picked up automatically by Spring's built-in
 * authentication system.
 */
@Component
@Primary
public class UserService implements UserDetailsService, UserDetailsPasswordService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails updatePassword(UserDetails userDetails, String newPassword) {
        if (!(userDetails instanceof User)) {
            throw new IllegalStateException("Cannot change password of UserDetails that isn't a User");
        }

        User user = (User) userDetails;
        user.setPassword(newPassword);
        return userRepository.save(user);
    }

    @Override
    public UserDetails loadUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("Unknown username"));
    }
}
