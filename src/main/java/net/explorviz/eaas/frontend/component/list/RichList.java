package net.explorviz.eaas.frontend.component.list;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * Implements a modern styled list that can contain unique items of the type {@code T}. A converter {@link Function} is
 * needed to convert the entries of type {@code T} to a {@link AbstractListEntry} that visually represents the entry.
 * Only unique entries can be added. Note that the {@link AbstractListEntry}s the converter function creates do not have
 * to be of the same type.
 * <p>
 * No components should be added through the inherited {@link #add(Component...)} methods.
 */
@Slf4j
@CssImport("./style/rich-list.css")
@Tag(Tag.UL)
public class RichList<T> extends VerticalLayout {
    private static final long serialVersionUID = 8287505649008791683L;

    private final Function<? super T, ? extends AbstractListEntry> converter;

    private final Map<T, AbstractListEntry> entries = new HashMap<>();

    // TODO: Redesign to support paging (without having to fetch all entries beforehand) or create PagedList<T>

    /**
     * @param converter Function used to create components that visually represent the added entries
     */
    public RichList(Function<? super T, ? extends AbstractListEntry> converter) {
        addClassName("rich-list");

        this.converter = converter;
    }

    /**
     * Add a single entry into this list. The function given in the constructor is used to create a visual {@link
     * Component} for it. Each entry may only be added once, i.e. the list can only contain unique entries. Trying to
     * add the same entry twice will log a warning but lead to no visual changes.
     */
    public void addEntry(T entry) {
        if (entries.containsKey(entry)) {
            // If we allowed this we would lose track of the previous AbstractListEntry
            log.warn("Tried to add an entry of type '{}' into a RichList twice. This is a bug",
                entry.getClass().getCanonicalName());
            return;
        }

        AbstractListEntry component = converter.apply(entry);
        entries.put(entry, component);
        add(component);
    }

    /**
     * Adds all the entries from the iterable.
     *
     * @see #addEntry(Object)
     */
    public void addEntries(Iterable<? extends T> entries) {
        entries.forEach(this::addEntry);
    }

    /**
     * Remove a previously added entry from this list. If the entry is not in the list, this method does nothing.
     */
    public void removeEntry(T entry) {
        AbstractListEntry component = entries.remove(entry);
        if (component != null) {
            remove(component);
        }
    }
}
