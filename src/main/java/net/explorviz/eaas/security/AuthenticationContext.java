package net.explorviz.eaas.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.server.ServiceInitEvent;
import com.vaadin.flow.server.VaadinServiceInitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class AuthenticationContext {
    /**
     * @param bcryptStrength See {@link BCryptPasswordEncoder#BCryptPasswordEncoder(int)}
     */
    @Bean
    @Lazy
    public PasswordEncoder standardPasswordEncoder(@Value("${eaas.security.bcryptStrength}") int bcryptStrength) {
        return new BCryptPasswordEncoder(bcryptStrength);
    }

    /**
     * Aside from {@link com.vaadin.flow.router.BeforeEnterObserver#beforeEnter(BeforeEnterEvent)} methods in the
     * views, this is what performs authentication checks in the frontend.
     * <p>
     * To do this, it creates a {@link VaadinServiceInitListener} that is used to register a
     * {@link UIEnterAuthenticator} to all newly initialized UIs that will prevent unauthenticated users from
     * entering secured views.
     */
    @Bean
    public VaadinServiceInitListener uiEnterAuthenticatorServiceListener() {
        return new UIEnterAuthenticatorServiceInitListener();
    }

    private static class UIEnterAuthenticatorServiceInitListener implements VaadinServiceInitListener {
        private static final long serialVersionUID = 2981945705881119187L;

        @Override
        public void serviceInit(ServiceInitEvent event) {
            event.getSource().addUIInitListener(uiEvent ->
                    uiEvent.getUI().addBeforeEnterListener(new UIEnterAuthenticator()));
        }
    }
}
