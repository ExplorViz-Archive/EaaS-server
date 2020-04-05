package net.explorviz.eaas.security;

import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.Secret;
import net.explorviz.eaas.model.repository.SecretRepository;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpStatus;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.server.ResponseStatusException;

import java.time.ZonedDateTime;
import java.util.Optional;

/**
 * Contains functions that help with authorizing API requests and determining access rights.
 */
@Component
@Lazy
public class APIAuthenticator {
    public static final String SECRET_HEADER = "X-EaaS-Secret";

    private final SecretRepository secretRepository;

    public APIAuthenticator(SecretRepository secretRepository) {
        this.secretRepository = secretRepository;
    }

    /**
     * Check if an access attempt to the API for a specific {@link Project} is authorized. If the access contains a
     * valid secret, its {@link Secret#getLastUsedDate() last used date} is updated to the current time, even if the
     * secret wasn't necessary to authorize the request.
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

        if (StringUtils.hasLength(secret)) {
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
     */
    private void useSecret(@NonNull Secret secret) {
        secret.setLastUsedDate(ZonedDateTime.now());
        secretRepository.save(secret);
    }
}
