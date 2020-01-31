package net.explorviz.eaas.frontend.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.dom.ElementFactory;
import com.vaadin.flow.router.Route;
import net.explorviz.eaas.service.explorviz.ExplorVizManager;
import net.explorviz.eaas.model.repository.ProjectRepository;
import net.explorviz.eaas.frontend.component.ExplorVizControlPanel;
import net.explorviz.eaas.frontend.component.ProjectList;

@Route
public class MainView extends VerticalLayout {
    private static final long serialVersionUID = -4417018497155730464L;

    public MainView(ProjectRepository projectRepo, ExplorVizManager explorVizManager) {
        add(new H2("ExplorViz as a Service"));
        getElement().appendChild(ElementFactory.createAnchor("/logout", "Logout"));

        HorizontalLayout horizontalLayout = new HorizontalLayout();
        horizontalLayout.add(new ExplorVizControlPanel(explorVizManager));
        horizontalLayout.add(new ProjectList(projectRepo));
        add(horizontalLayout);
    }
}
