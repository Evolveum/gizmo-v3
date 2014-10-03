package sk.lazyman.gizmo.component.data;

import org.apache.wicket.extensions.markup.html.repeater.data.sort.ISortStateLocator;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByBorder;
import org.apache.wicket.extensions.markup.html.repeater.data.sort.OrderByLink;

/**
 * @author lazyman
 */
public abstract class BasicOrderByBorder extends OrderByBorder {

    protected BasicOrderByBorder(String id, Object property, ISortStateLocator stateLocator) {
        super(id, property, stateLocator, BasicCssProvider.getInstance());
    }

    @Override
    protected OrderByLink newOrderByLink(String id, Object property, ISortStateLocator stateLocator) {
        return new OrderByLink(id, property, stateLocator, new OrderByLink.VoidCssProvider()) {

            @Override
            protected void onSortChanged() {
                BasicOrderByBorder.this.onSortChanged();
            }
        };
    }

    public static class BasicCssProvider extends OrderByLink.CssProvider {

        private static BasicCssProvider instance = new BasicCssProvider();

        private BasicCssProvider() {
            super("sortable asc", "sortable desc", "sortable");
        }

        public static BasicCssProvider getInstance() {
            return instance;
        }
    }
}
