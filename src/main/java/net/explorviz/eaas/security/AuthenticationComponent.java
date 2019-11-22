package net.explorviz.eaas.security;

import net.explorviz.eaas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationComponent {
    private final UserRepository userRepository;

    /**
     * @see BCryptPasswordEncoder#BCryptPasswordEncoder(int)
     */
    @Value("${eaas.security.bcryptStrength:12}")
    private int bcryptStrength;

    /**
     * @see KeyGenerator#KeyGenerator(int, int)
     */
    @Value("${eaas.security.defaultPasswordLength:16}")
    private int defaultPasswordLength;

    /**
     * @see KeyGenerator#KeyGenerator(int, int)
     */
    @Value("${eaas.security.apiKeyBytes:16}")
    private int apiKeyBytes;

    public AuthenticationComponent(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }

    @Bean
    public KeyGenerator keyGenerator() {
        return new KeyGenerator(defaultPasswordLength, apiKeyBytes);
    }

    /**
     * Note: This {@link UserDetailsService} implementation will automatically be picked up by Spring's built-in
     * authentication system.
     */
    @Bean
    public UserService userService() {
        return new UserService(userRepository);
    }
}
