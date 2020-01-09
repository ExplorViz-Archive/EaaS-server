package net.explorviz.eaas.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class AuthenticationComponent {
    /**
     * @see BCryptPasswordEncoder#BCryptPasswordEncoder(int)
     */
    @Value("${eaas.security.bcryptStrength:12}")
    private int bcryptStrength;

    @Bean
    public PasswordEncoder standardPasswordEncoder() {
        return new BCryptPasswordEncoder(bcryptStrength);
    }
}
