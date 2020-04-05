package net.explorviz.eaas.security;

import net.explorviz.eaas.model.entity.Secret;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;

/**
 * Generate strings used for authentication from a secure randomness source.
 */
@Component
@Lazy
public class KeyGenerator {
    protected final int passwordLength;
    protected final int apiKeyBytes;

    protected final SecureRandom secureRandom;

    /**
     * @param passwordLength How long (in characters) generated passwords should be. Must be positive and non-zero.
     * @param apiKeyBytes    How many bytes generated API keys should have. Must be positive and non-zero.
     */
    public KeyGenerator(@Value("${eaas.security.defaultPasswordLength}") int passwordLength,
                        @Value("${eaas.security.apiKeyBytes}") int apiKeyBytes) {
        Validate.isTrue(passwordLength > 0, "Option eaas.security.defaultPasswordLength must be positive: %d",
            passwordLength);
        int apiKeyMaxBytes = Secret.SECRET_MAX_LENGTH / 2;
        Validate.inclusiveBetween(1, apiKeyMaxBytes, apiKeyBytes, "Option eaas.security.apiKeyBytes " +
            "must be between 1 and " + apiKeyMaxBytes);

        this.passwordLength = passwordLength;
        this.apiKeyBytes = apiKeyBytes;

        this.secureRandom = new SecureRandom();
    }

    /**
     * Generate a secure random alphanumerical string of the requested length.
     *
     * @return the generated string
     */
    public String generatePassword() {
        return RandomStringUtils.random(passwordLength, 0, 0, true, true, null, secureRandom);
    }

    /**
     * Generate a secure random hexadecimal string containing {@code apiKeyBytes} bytes of entropy. The strings will be
     * twice this number in length (because of hexadecimal encoding).
     *
     * @return the generated string
     */
    public String generateAPIKey() {
        byte[] secretBytes = new byte[apiKeyBytes];
        secureRandom.nextBytes(secretBytes);

        StringBuilder secret = new StringBuilder(apiKeyBytes * 2);
        for (byte b : secretBytes) {
            secret.append(String.format("%02x", b));
        }
        return secret.toString();
    }
}
