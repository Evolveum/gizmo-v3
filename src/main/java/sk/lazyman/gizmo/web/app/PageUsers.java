package sk.lazyman.gizmo.web.app;

import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.LinkColumn;
import sk.lazyman.gizmo.component.TablePanel;
import sk.lazyman.gizmo.component.UsersDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/users")
public class PageUsers extends PageAppTemplate {

    private static final String ID_TABLE = "table";

    public PageUsers() {
        initLayout();
    }

    private void initLayout() {
        SortableDataProvider provider = new UsersDataProvider(getUserRepository());
        List<IColumn> columns = new ArrayList<>();

        columns.add(new LinkColumn(createStringResource("Username"), "userName"));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 20);
        add(table);
    }
}
