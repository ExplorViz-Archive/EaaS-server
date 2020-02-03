package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

public class SidebarFooter extends VerticalLayout {
    private static final long serialVersionUID = 812136038394838713L;

    public SidebarFooter() {
        add(new Anchor("/logout", "Logout"));
        add(new Anchor("https://www.explorviz.net", "explorviz.net"));
    }
}
