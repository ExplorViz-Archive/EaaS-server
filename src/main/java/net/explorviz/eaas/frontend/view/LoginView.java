package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.component.ExplorVizBanner;
import net.explorviz.eaas.security.SecurityUtils;

import java.util.Collections;

@Tag("sa-login-view")
@Route(LoginView.ROUTE)
@PageTitle("Login - " + Application.PAGE_TITLE)
@CssImport("./style/login.css")
public class LoginView extends VerticalLayout implements BeforeEnterObserver {
    private static final long serialVersionUID = 1010867826588360870L;

    public static final String ROUTE = "login";

    private final LoginForm login = new LoginForm();

    public LoginView() {
        add(new ExplorVizBanner(true));

        login.setAction(ROUTE);
        login.setForgotPasswordButtonVisible(false);
        add(login);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if (SecurityUtils.isUserLoggedIn()) {
            event.forwardTo(MainView.class);
        }

        if (!event.getLocation().getQueryParameters().getParameters()
            .getOrDefault("error", Collections.emptyList()).isEmpty()) {
            login.setError(true);
        }
    }
}
