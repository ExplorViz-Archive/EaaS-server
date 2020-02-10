package net.explorviz.eaas.model;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.time.ZonedDateTime;
import java.util.Optional;

@Configuration
@EnableJpaAuditing(dateTimeProviderRef = "dateTimeProvider"/*, auditorAwareRef = "userAuditorProvider"*/)
@EnableJpaRepositories
@EnableTransactionManagement
public class PersistenceConfiguration {
    // /**
    //  * Provides the current user for auditing purposes from {@link SecurityUtils#getCurrentUser()}
    //  */
    // @Bean
    // public AuditorAware<User> userAuditorProvider() {
    //     return SecurityUtils::getCurrentUser;
    // }

    /**
     * Provides the current time from {@link ZonedDateTime#now()}.
     */
    @Bean
    public DateTimeProvider dateTimeProvider() {
        return () -> Optional.of(ZonedDateTime.now());
    }
}
