package net.explorviz.eaas.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import net.explorviz.eaas.frontend.view.LoginView;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Component;

/**
 * Adds a {@link com.vaadin.flow.router.BeforeEnterListener} to all newly initialized UIs that will prevent
 * unauthenticated users from entering secured views.
 *
 * @see <a href="https://vaadin.com/tutorials/securing-your-app-with-spring-security/setting-up-spring-security">Setting
 * up Spring Security for Vaadin applications</a>
 */
@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
    private static final long serialVersionUID = -6787242758333743373L;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent ->
            uiEvent.getUI().addBeforeEnterListener(ConfigureUIServiceInitListener::beforeEnter));
    }

    /**
     * Reroutes the user to the {@link LoginView} if they're not logged in. Also checks for {@link Secured} annotations
     * on views about to be entered and checks if the current security context has the necessary authority.
     */
    private static void beforeEnter(BeforeEnterEvent event) {
        Class<?> target = event.getNavigationTarget();

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
