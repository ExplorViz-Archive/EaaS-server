package net.explorviz.eaas.security;

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

import java.util.Arrays;
import java.util.Optional;

/**
 * Static helper methods for dealing with authentication. The current security context is fetched from the {@link
 * SecurityContextHolder}, which is usually a thread-local, i.e. it is specific to the current web request.
 */
public final class SecurityUtils {
    /**
     * Utility class cannot be initialized.
     */
    private SecurityUtils() {
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
     * Tests if there is a user logged in. This required that the current security context is authenticated, that the
     * authentication is not an implicit anonymous one and the principal is a {@link User} from our database.
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
     * Tests if the current security context has the given authority. For performance reasons, prefer the {@link
     * #hasAuthority(GrantedAuthority)} method over this one if you can.
     */
    public static boolean hasAuthority(@NonNull String authority) {
        Validate.notBlank(authority, "authority may not be empty");

        return getCurrentAuthentication().map(auth ->
            auth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch(a -> a.equals(authority))
        ).orElse(false);
    }

    /**
     * Tests if the current security context may access the given view at all. Access may be denied if a {@link Secured}
     * annotation is present on the view and the current security context does not have any of the necessary authority.
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
     * Tests if the current security context may read from the given project, i.e. if this method returns {@code false}
     * the client must be presented with an access denied error page or asked to log in if they aren't already.
     * <p>
     * Access is forbidden if the principal of the current security context does not hold any READ authorities that
     * apply for the given project.
     */
    public static boolean hasReadAccess(@NonNull Project project) {
        if (hasAuthority(Authorities.READ_ALL_PROJECTS_AUTHORITY)) {
            return true;
        }

        return hasAuthority(Authorities.READ_OWNED_PROJECTS_AUTHORITY)
            && getCurrentUser().map(user -> user.getId().equals(project.getOwner().getId())).orElse(false);
    }

    /**
     * Tests if the current security context may manage the given project, i.e. change settings or delete it.
     * <p>
     * Access is forbidden if the principal of the current security context does not hold any MANAGE authorities that
     * apply for the given project.
     */
    public static boolean hasManageAccess(@NonNull Project project) {
        if (hasAuthority(Authorities.MANAGE_ALL_PROJECTS_AUTHORITY)) {
            return true;
        }

        return hasAuthority(Authorities.MANAGE_OWNED_PROJECTS_AUTHORITY)
            && getCurrentUser().map(user -> user.getId().equals(project.getOwner().getId())).orElse(false);
    }
}
