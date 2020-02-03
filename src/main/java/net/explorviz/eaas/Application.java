package net.explorviz.eaas;

import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.model.entity.User;
import net.explorviz.eaas.model.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.web.servlet.error.ErrorMvcAutoConfiguration;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * The entry point of the Spring Boot application.
 */
@SpringBootApplication(exclude = ErrorMvcAutoConfiguration.class)
@Slf4j
public class Application implements CommandLineRunner {
    public static final String PAGE_TITLE = "ExplorViz as a Service";

    private static final String DEFAULT_USERNAME = "admin";
    private static final String DEFAULT_PASSWORD = "password";

    private final UserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    public Application(UserRepository userRepo, PasswordEncoder passwordEncoder) {
        this.userRepo = userRepo;
        this.passwordEncoder = passwordEncoder;
    }

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Override
    public void run(String... args) {
        if (userRepo.count() == 0L) {
            userRepo.save(new User(DEFAULT_USERNAME, passwordEncoder.encode(DEFAULT_PASSWORD), true));

            log.info("This looks like a fresh installation! Added a user with administrator permissions.\n" +
                    "\n" +
                    "Login as user '{}' with password '{}'\n",
                DEFAULT_USERNAME, DEFAULT_PASSWORD);
        }
    }
}
