package sk.lazyman.gizmo.component;

import org.apache.commons.lang.Validate;
import org.apache.wicket.AttributeModifier;
import org.apache.wicket.Component;
import org.apache.wicket.extensions.markup.html.repeater.data.table.DataTable;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.List;

/**
 * @author lazyman
 */
public class TablePanel<T> extends Panel {

    private static final String ID_TABLE = "table";
    private static final String ID_PAGING = "paging";

    private IModel<Boolean> showPaging = new Model<Boolean>(true);
    private IModel<Boolean> showCount = new Model<Boolean>(true);

    public TablePanel(String id, ISortableDataProvider provider, List<IColumn<T, String>> columns, int rowsPerPage) {
        super(id);
        Validate.notNull(provider, "Provider must not be null.");
        Validate.notNull(columns, "Columns must not be null.");

        add(AttributeModifier.prepend("style", "display: table; width: 100%;"));

        initLayout(columns, provider, rowsPerPage);
    }

    private void initLayout(List<IColumn<T, String>> columns, ISortableDataProvider provider, int rowsPerPage) {
        DataTable<T, String> table = new DataTable<>(ID_TABLE, columns, provider, rowsPerPage);

        table.setOutputMarkupId(true);

        TableHeadersToolbar headers = new TableHeadersToolbar(table, provider);
        headers.setOutputMarkupId(true);
        table.addTopToolbar(headers);

        CountToolbar count = new CountToolbar(table);
        addVisibleBehaviour(count, showCount);
        table.addBottomToolbar(count);

        add(table);

        NavigatorPanel nb2 = new NavigatorPanel(ID_PAGING, table, true);
        addVisibleBehaviour(nb2, showPaging);
        add(nb2);
    }

    private void addVisibleBehaviour(Component comp, final IModel<Boolean> model) {
        comp.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return model.getObject();
            }
        });
    }

    public DataTable getDataTable() {
        return (DataTable) get(ID_TABLE);
    }

    public NavigatorPanel getNavigatorPanel() {
        return (NavigatorPanel) get(ID_PAGING);
    }

    public void setItemsPerPage(int size) {
        getDataTable().setItemsPerPage(size);
    }

    public void setCurrentPage(Long page) {
        if (page == null) {
            getDataTable().setCurrentPage(0);
            return;
        }

        getDataTable().setCurrentPage(page);
    }

    public void setShowPaging(boolean showPaging) {
        this.showPaging.setObject(showPaging);
        this.showCount.setObject(showPaging);

        if (!showPaging) {
            setItemsPerPage(Integer.MAX_VALUE);
        } else {
            setItemsPerPage(10);
        }
    }

    public void setShowCount(boolean showCount) {
        this.showCount.setObject(showCount);
    }
}
