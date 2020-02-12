package net.explorviz.eaas.frontend.component;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;

import java.util.function.Function;

@CssImport("./style/simple-list.css")
public class SimpleList<T> extends VerticalLayout {
    private static final long serialVersionUID = 8287505649008791683L;

    // TODO: Redesign to have paging (without having to fetch all Projects)

    public SimpleList(Iterable<? extends T> entries, Function<? super T, ? extends Component> converter) {
        entries.forEach(entry -> add(converter.apply(entry)));
    }
}
