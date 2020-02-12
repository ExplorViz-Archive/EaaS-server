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
 * Checks for {@link Secured} annotations on views about to be entered and checks if the current security context has
 * the necessary authority. Otherwise the client is rerouted to the {@link LoginView}. If they're already logged in they
 * get an access denied error.
 * <p>
 * Note: You should not add {@link Secured} annotations to error views (i.e. they implement {@link HasErrorParameter}),
 * because then users might be asked to log in only to be presented with another error, which is bad UX.
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

        if (!SecurityUtils.mayAccess(target)) {
            if (SecurityUtils.isUserLoggedIn()) {
                log.debug("User is not authorized to enter view: {}", target.getCanonicalName());
                throw new AccessDeniedException("You do not have permission to access this page.");
            } else {
                log.debug("Login required to access view: {}", target.getCanonicalName());
                event.rerouteTo(LoginView.class);
            }
        }
    }
}
