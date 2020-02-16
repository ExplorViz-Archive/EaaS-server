package net.explorviz.eaas.frontend.view.error;

import com.vaadin.flow.component.icon.VaadinIcon;
import org.springframework.security.access.AccessDeniedException;

import javax.servlet.http.HttpServletResponse;

public class AccessDeniedView extends AbstractErrorView<AccessDeniedException> {
    private static final long serialVersionUID = 1488319690983423988L;

    public AccessDeniedView() {
        super(HttpServletResponse.SC_FORBIDDEN, "Access Denied", "You do not have permission to access this page.",
            VaadinIcon.EXCLAMATION_CIRCLE_O.create());
    }
}
