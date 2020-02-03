package net.explorviz.eaas.security;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.ServletRequest;
import java.util.Arrays;
import java.util.Optional;
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

    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication());
    }

    /**
     * Tests if there is a user currently logged in.
     */
    public static boolean isUserLoggedIn() {
        return getCurrentAuthentication().map(auth ->
            !(auth instanceof AnonymousAuthenticationToken) && auth.isAuthenticated()
        ).orElse(false);
    }

    /**
     * Tests if the current security context has the given authority.
     */
    public static boolean hasAuthority(@NonNull GrantedAuthority authority) {
        return getCurrentAuthentication().map(auth ->
            auth.getAuthorities().contains(authority)
        ).orElse(false);
    }

    /**
     * Tests if the current security context has the given authority.
     */
    public static boolean hasAuthority(@NonNull String authority) {
        return getCurrentAuthentication().map(auth ->
            auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals(authority))
        ).orElse(false);
    }

    /**
     * Tests if the current security context may access the given view at all. Access may be denied if a {@link Secured}
     * annotation is present on the view and the current security context does not have the necessary authority.
     * <p>
     * Even if the user may generally enter a view, the specific implementation can have additional authority checks
     * before allowing the user to enter, e.g. based on the parameter. Therefore this method can not be used to
     * authoratively determine the users permission to enter a specific view.
     */
    public static boolean mayAccess(@NonNull Class<?> clazz) {
        Secured secured = AnnotationUtils.findAnnotation(clazz, Secured.class);
        return secured == null || Arrays.stream(secured.value()).anyMatch(SecurityUtils::hasAuthority);
    }
}
