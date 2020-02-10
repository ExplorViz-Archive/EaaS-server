package net.explorviz.eaas.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterListener;
import com.vaadin.flow.router.HasErrorParameter;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.frontend.view.LoginView;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;

/**
 * Checks if the current security context has the necessary permission before entering views.
 * <p>
 * E.g. it reroutes the user to the {@link LoginView} if they're not logged in. Also checks for {@link Secured}
 * annotations on views about to be entered and checks if the current security context has the necessary authority.
 *
 * @see <a href="https://vaadin.com/tutorials/securing-your-app-with-spring-security/setting-up-spring-security">Setting
 * up Spring Security for Vaadin applications</a>
 */
@Slf4j
public class UIEnterAuthenticator implements BeforeEnterListener {
    private static final long serialVersionUID = 8601163001283809803L;

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        Class<?> target = event.getNavigationTarget();

        log.debug("About to enter view {}", target.getCanonicalName());

        // Error pages can be public
        if (HasErrorParameter.class.isAssignableFrom(target)) {
            log.debug("Allow entering view {} because it is an error page", target.getCanonicalName());
            return;
        }

        // TODO: Allow a public front page
        if (!SecurityUtils.isUserLoggedIn() && !LoginView.class.equals(target)) {
            event.rerouteTo(LoginView.class);
            return;
        }

        if (!SecurityUtils.mayAccess(target)) {
            throw new AccessDeniedException("You do not have permission to access this page.");
        }
    }
}
