package sk.lazyman.gizmo.component;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.HeadersToolbar;
import org.apache.wicket.markup.html.WebMarkupContainer;

/**
 * @author lazyman
 */
public class TableHeadersToolbar<T> extends HeadersToolbar {

    public TableHeadersToolbar(DataTable<T, String> table, ISortStateLocator stateLocator) {
        super(table, stateLocator);
    }

    @Override
    protected WebMarkupContainer newSortableHeader(String headerId, Object property, ISortStateLocator locator) {
        return new BasicOrderByBorder(headerId, property, locator) {

            @Override
            protected void onSortChanged() {
                getTable().setCurrentPage(0);
            }
        };
    }
}
