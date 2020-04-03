package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;
import net.explorviz.eaas.service.process.BackgroundProcess;
import net.explorviz.eaas.service.process.ProcessListener;

import java.util.function.Consumer;

import static com.vaadin.flow.dom.ElementFactory.createHeading3;
import static com.vaadin.flow.dom.ElementFactory.createPreformatted;

/**
 * Implements {@link ProcessListener} to show the output of a {@link BackgroundProcess} to the client in real-time.
 * <p>
 * Views using this need to use an {@link com.vaadin.flow.component.applayout.AppLayout} annotated with {@link
 * com.vaadin.flow.component.page.Push} or updates won't work in real-time.
 */
@Slf4j
@CssImport("./style/dialog.css")
public class LogDialog extends Dialog implements ProcessListener {
    private static final long serialVersionUID = -7229177487266728104L;

    private final Consumer<? super LogDialog> closeCallback;
    private final UI ui;

    private final VerticalLayout output;

    private volatile boolean stopped;

    /**
     * @param closeCallback Will be called when the dialog is being closed by the user
     */
    public LogDialog(String dialogTitle, UI ui, Consumer<? super LogDialog> closeCallback) {
        this.ui = ui;
        this.closeCallback = closeCallback;

        addDialogCloseActionListener(ignored -> this.onDialogCloseAction());

        Button closeButton = new Button();
        closeButton.setIcon(VaadinIcon.CLOSE.create());
        closeButton.addClickListener(ignored -> onDialogCloseAction());

        HorizontalLayout header = new HorizontalLayout();
        header.setId("dialog-header");
        header.add(closeButton);
        header.getElement().appendChild(createHeading3("Output of " + dialogTitle));
        add(header);

        output = new VerticalLayout();
        output.setId("log-output");
        add(output);
    }

    private void onDialogCloseAction() {
        // Use a boolean flag so we can close the dialog immediately, then do cleanup for a nicer UX
        stopped = true;
        close();
        closeCallback.accept(this);
    }

    private void appendText(String text) {
        /*
         * TODO: There is a possible throughput improvement by merging BackgroundProcess#ProcessObserver into this
         *  class so we can keep reading from the standard output blockingly until the previous #access() went through.
         *  Then we could also control the total amount of lines displayed in the log more easily.
         */
        if (!stopped) {
            /*
             * Try to do cleanup if the user closed the tab (i.e. we didn't receive a BeforeLeaveEvent).
             * If we fail to kill the BackgroundProcess it's not that big of a deal, the process will die in any
             * case when the build instance is stopped.
             */
            if (ui.isClosing()) {
                closeCallback.accept(this);
                return;
            }

            ui.access(() -> {
                output.getElement().appendChild(createPreformatted(text));
                // TODO: Auto-scroll and limit history
                ui.push();
            });
        }
    }

    @Override
    public void onDied(int exitCode) {
        appendText("\nEnd of output");
    }

    @Override
    public void onStandardOutput(String text) {
        appendText(text);
    }
}
