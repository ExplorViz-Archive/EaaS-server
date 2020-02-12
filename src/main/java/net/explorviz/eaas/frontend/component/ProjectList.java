package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import net.explorviz.eaas.model.entity.Project;

public class ProjectList extends VerticalLayout {
    private static final long serialVersionUID = 8287505649008791683L;

    public ProjectList(Iterable<? extends Project> projects) {
        projects.forEach(project -> add(new ProjectListEntry(project)));
    }
}
