package net.explorviz.eaas;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

@Route
public class MainView extends VerticalLayout {
    public MainView(@Autowired MessageBean bean) {
        add(new H1("ExplorViz as a Service"));

        VerticalLayout projects = new VerticalLayout();
        projects.add(new H3("Projects"));
        add(projects);

        TextField projectName = new TextField();
        Button createProject = new Button("Create new project");
        createProject.addClickListener(click -> {
            String name = projectName.getValue();
            if (StringUtils.isBlank(name)) {
                Notification.show(bean.getEmptyNameMessage());
            } else {
                projects.add(new Paragraph(name));
                Notification.show(bean.getCreatedMessage(name));
            }
        });

        add(new HorizontalLayout(projectName, createProject));
    }
}
