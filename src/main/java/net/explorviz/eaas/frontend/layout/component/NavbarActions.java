package net.explorviz.eaas.frontend.layout.component;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import net.explorviz.eaas.frontend.component.IconAnchor;
import net.explorviz.eaas.security.SecurityConfiguration;
import net.explorviz.eaas.security.SecurityUtils;

public class NavbarActions extends HorizontalLayout {
    private static final long serialVersionUID = 812136038394838713L;

    public NavbarActions() {
        setId("navbar-actions");

        Anchor homepage = IconAnchor.createFromImage("https://www.explorviz.net", "icons/icon-48x48.png",
            "explorviz.net");
        homepage.setTarget("_blank");
        add(homepage);

        if (SecurityUtils.isUserLoggedIn()) {
            add(IconAnchor.createFromIcon(SecurityConfiguration.LOGOUT_URL, VaadinIcon.SIGN_OUT, "Logout"));
        } else {
            add(IconAnchor.createFromIcon(SecurityConfiguration.LOGIN_URL, VaadinIcon.SIGN_IN, "Login"));
        }
    }
}
