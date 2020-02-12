package net.explorviz.eaas.security;

import com.vaadin.flow.server.ServletHelper;
import com.vaadin.flow.shared.ApplicationConstants;
import net.explorviz.eaas.model.entity.Project;
import net.explorviz.eaas.model.entity.User;
import org.apache.commons.lang3.Validate;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.lang.NonNull;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
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
    public static boolean isVaadinInternalRequest(@NonNull ServletRequest request) {
        final String parameterValue = request.getParameter(ApplicationConstants.REQUEST_TYPE_PARAMETER);
        return parameterValue != null
            && Stream.of(ServletHelper.RequestType.values()).anyMatch(r -> r.getIdentifier().equals(parameterValue));
    }

    /**
     * Returns the current authentication if and only if it is authenticated.
     *
     * @see SecurityContext#getAuthentication()
     * @see Authentication#isAuthenticated()
     */
    public static Optional<Authentication> getCurrentAuthentication() {
        return Optional.ofNullable(SecurityContextHolder.getContext().getAuthentication())
            .filter(Authentication::isAuthenticated);
    }

    /**
     * Returns the currently authenticated user, i.e. the principal of the currently active authentication.
     * <p>
     * Returns non-empty if and only if the current security context is authenticated and the principal is a User from
     * our database.
     */
    public static Optional<User> getCurrentUser() {
        /*
         * We need to check #isUserLoggedIn() beforehand, because even an AnonymousAuthenticationToken is considered
         * authenticated, and it can even have a principal.
         */
        if (!isUserLoggedIn()) {
            return Optional.empty();
        }

        return getCurrentAuthentication().map(auth -> (User) auth.getPrincipal());
    }

    /**
     * Tests if there is an authenticated security context, that it is not an implicit anonymous one and the principal
     * is a {@link User} from our database.
     */
    public static boolean isUserLoggedIn() {
        return getCurrentAuthentication().map(auth ->
            !(auth instanceof AnonymousAuthenticationToken) && auth.getPrincipal() instanceof User
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
        Validate.notBlank(authority, "authority may not be empty");

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
     * authoritatively determine the users permission to enter a specific view.
     */
    public static boolean mayAccess(@NonNull Class<?> clazz) {
        Secured secured = AnnotationUtils.findAnnotation(clazz, Secured.class);
        return secured == null || Arrays.stream(secured.value()).anyMatch(SecurityUtils::hasAuthority);
    }

    /**
     * Tests if the current security context may access the given project at all, i.e. if this method returns {@code
     * false} the client must be presented with an access denied error page or asked to log in if they aren't already.
     * <p>
     * Access may be forbidden if the project is hidden ({@link Project#isHidden()} and the principal of the current
     * security context is not the owner ({@link Project#getOwner()}) of the project.
     */
    public static boolean mayAccessProject(Project project) {
        if (!project.isHidden()) {
            return true;
        }

        return getCurrentUser().map(user -> user.getId().equals(project.getOwner().getId())).orElse(false);
    }
}
