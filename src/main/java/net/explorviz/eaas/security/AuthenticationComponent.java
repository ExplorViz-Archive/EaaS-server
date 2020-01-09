package net.explorviz.eaas.security;

import net.explorviz.eaas.repository.SecretRepository;
import net.explorviz.eaas.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.annotation.Primary;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationComponent {
    private final UserRepository userRepository;
    private final SecretRepository secretRepository;

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

    public AuthenticationComponent(UserRepository userRepository, SecretRepository secretRepository) {
        this.userRepository = userRepository;
        this.secretRepository = secretRepository;
    }

    @Bean
    public APIAuthenticator standardAPIAuthenticator() {
        return new APIAuthenticator(secretRepository);
    }

    @Bean
    public PasswordEncoder standardPasswordEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }

    @Bean
    @Lazy
    public KeyGenerator keyGenerator() {
        return new KeyGenerator(defaultPasswordLength, apiKeyBytes);
    }

    /**
     * Note: This {@link UserDetailsService} implementation will automatically be picked up by Spring's built-in
     * authentication system.
     */
    @Bean
    @Primary
    public UserService eaasUserService() {
        return new UserService(userRepository);
    }
}
