package net.explorviz.eaas.security;

import net.explorviz.eaas.model.Project;
import net.explorviz.eaas.model.Secret;
import net.explorviz.eaas.repository.SecretRepository;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.util.Optional;

/**
 * Contains functions that help with authorizing API requests and determining access rights.
 */
@Component
@Lazy
public class APIAuthenticator {
    public static final String SECRET_HEADER_NAME = "X-EaaS-Secret";

    private final SecretRepository secretRepository;

    public APIAuthenticator(SecretRepository secretRepository) {
        this.secretRepository = secretRepository;
    }

    /**
     * Check if an access attempt to the API for a specific {@link Project} is authorized.
     * If the access contains a valid secret, its {@link Secret#getLastUsedDate() last used date} is updated to the
     * current time, even if the secret wasn't necessary to authorize the request.
     * <p>
     * The following access attempts are allowed:
     * <ul>
     *     <li>Trying to read from a non-hidden project (even if an invalid secret is specified)</li>
     *     <li>Trying to read from a hidden project with a valid secret</li>
     *     <li>Trying to write to a project with a valid secret</li>
     * </ul>
     * In any other case, a {@link ResponseStatusException} with the appropriate {@link HttpStatus} is thrown.
     *
     * @param project  The project the request is for
     * @param secret   Optionally, a secret the request might contain
     * @param readonly Whether the request only reads information
     */
    public void authorizeRequest(@NonNull Project project, @Nullable String secret, boolean readonly) {
        Optional<Secret> optionalSecret = Optional.empty();

        if (!StringUtils.isEmpty(secret)) {
            // TODO: Secret comparisons are not timing attack resistant
            optionalSecret = secretRepository.findByProjectAndSecret(project, secret);
            optionalSecret.ifPresent(this::useSecret);
        }

        if (readonly && !project.isHidden()) {
            return; // Access is allowed
        }

        if (StringUtils.isEmpty(secret)) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Secret required but none given");
        } else if (optionalSecret.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Given secret is invalid");
        }
        // Access is allowed
    }

    /**
     * Sets the {@link Secret#getLastUsedDate() last used date} for the {@link Secret} to the current time.
     *
     * @return The modified secret object with the new last used date
     */
    private Secret useSecret(@NonNull Secret secret) {
        secret.setLastUsedDate(Instant.now());
        return secretRepository.save(secret);
    }
}
