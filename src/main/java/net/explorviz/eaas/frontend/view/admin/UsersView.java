package net.explorviz.eaas.frontend.view.admin;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.Application;
import net.explorviz.eaas.frontend.layout.MainLayout;
import net.explorviz.eaas.frontend.view.DynamicView;
import net.explorviz.eaas.model.repository.UserRepository;
import org.springframework.security.access.annotation.Secured;

@PageTitle("Users - " + Application.PAGE_TITLE)
@Route(value = "users", layout = MainLayout.class)
@Secured("ADMINISTER")
public class UsersView extends DynamicView {
    private static final long serialVersionUID = 4767534558255466871L;

    private final UserRepository userRepository;

    public UsersView(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    protected void build() {
        add(new H2("Users"));

        //add(new UserList(userRepository));
    }
}
