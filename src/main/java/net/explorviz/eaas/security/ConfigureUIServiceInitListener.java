package net.explorviz.eaas.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import net.explorviz.eaas.view.LoginView;
import org.springframework.stereotype.Component;

/**
 * @see <a href="https://vaadin.com/tutorials/securing-your-app-with-spring-security/setting-up-spring-security#_secure_router_navigation">Setting
 * up Spring Security for Vaadin applications</a>
 */
@Component
public class ConfigureUIServiceInitListener implements VaadinServiceInitListener {
    private static final long serialVersionUID = -6787242758333743373L;

    @Override
    public void serviceInit(ServiceInitEvent event) {
        event.getSource().addUIInitListener(uiEvent -> {
            uiEvent.getUI().addBeforeEnterListener(ConfigureUIServiceInitListener::beforeEnter);
        });
    }

    /**
     * Reroutes the user if (s)he is not authorized to access the view.
     *
     * @param event before navigation event with event details
     */
    private static void beforeEnter(BeforeEnterEvent event) {
        if (!LoginView.class.equals(event.getNavigationTarget())
            && !SecurityUtils.isUserLoggedIn()) {
            event.rerouteTo(LoginView.class);
        }
        // TODO: Verify authorities before letting users enter a view
    }
}
