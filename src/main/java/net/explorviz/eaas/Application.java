package net.explorviz.eaas;

import net.explorviz.eaas.model.User;
import net.explorviz.eaas.repository.UserRepository;
import net.explorviz.eaas.security.KeyGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
public class Application implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(Application.class);

    private static final String DEFAULT_USER_NAME = "admin";

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;
    private final KeyGenerator keyGenerator;

    public Application(UserRepository userRepo, PasswordEncoder passwordEncoder, KeyGenerator keyGenerator) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
        this.keyGenerator = keyGenerator;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0L) {
            String password = keyGenerator.generatePassword();
            userRepo.save(new User(DEFAULT_USER_NAME, passwordEncoder.encode(password), true));

            logger.info("This looks like a fresh installation! Added a user with administrator permissions.\n" +
                    "\n" +
                    "Login as user '{}' with password '{}'\n",
                DEFAULT_USER_NAME, password);
        }
    }
}
