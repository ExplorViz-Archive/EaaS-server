package net.explorviz.eaas.security;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletRequest;
import java.util.stream.Stream;

/**
 * Static helper methods for dealing with authentication.
 */
public final class SecurityUtils {
    /**
     * Utility class.
     */
    private SecurityUtils() {
    }

    /**
     * Tests if the request is an internal Vaadin request by checking if the request contains Vaadins' request type
     * parameter and its value is any of the known request type.
     *
     * @param request {@link ServletRequest}
     * @return true if is an internal Vaadin request
     */
    public static boolean isVaadinInternalRequest(ServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
            && Stream.of(ServletHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    /**
     * Tests if there is a user currently logged in.
     */
    public static boolean isUserLoggedIn() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return authentication != null
            && !(authentication instanceof AnonymousAuthenticationToken)
            && authentication.isAuthenticated();
    }
}
