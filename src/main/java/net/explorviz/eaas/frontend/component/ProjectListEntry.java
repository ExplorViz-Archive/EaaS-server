package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.RouterLink;
import lombok.Getter;
import net.explorviz.eaas.frontend.view.project.BuildsView;
import net.explorviz.eaas.model.entity.Project;

import java.util.function.Consumer;

public class ProjectListEntry extends VerticalLayout {
    private static final long serialVersionUID = 1589947241429733513L;

    @Getter
    private final Project project;

    public ProjectListEntry(Project project, Consumer<? super ProjectListEntry> deleteListener) {
        this.project = project;

        add(new RouterLink(project.getName() + "(#" + project.getId() + ")", BuildsView.class, project.getId()));
        add(new Paragraph("Created on " + project.getCreatedDate()));

        Button deleteButton = new Button("Delete");
        deleteButton.addClickListener(click -> deleteListener.accept(this));
        add(deleteButton);
    }
}
