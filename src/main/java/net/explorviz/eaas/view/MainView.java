package net.explorviz.eaas.view;

import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.repository.ProjectRepository;
import net.explorviz.eaas.view.component.ProjectList;
import org.springframework.beans.factory.annotation.Autowired;

@Route
public class MainView extends VerticalLayout {
    private static final long serialVersionUID = -4417018497155730464L;

    public MainView(@Autowired ProjectRepository projectRepo) {
        add(new H1("ExplorViz as a Service"));
        getElement().appendChild(ElementFactory.createAnchor("/logout", "Logout"));
        add(new ProjectList(projectRepo));
    }
}
