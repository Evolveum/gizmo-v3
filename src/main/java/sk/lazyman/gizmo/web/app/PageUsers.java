package sk.lazyman.gizmo.web.app;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.LinkColumn;
import sk.lazyman.gizmo.component.TablePanel;
import sk.lazyman.gizmo.component.UsersDataProvider;
import sk.lazyman.gizmo.data.User;

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

        columns.add(new LinkColumn<User>(createStringResource("PageUsers.table.username"), "userName") {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<User> rowModel) {
                userDetailsPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new PropertyColumn(createStringResource("PageUsers.table.firstName"), "firstName"));
        columns.add(new PropertyColumn(createStringResource("PageUsers.table.lastName"), "lastName"));
        columns.add(new PropertyColumn(createStringResource("PageUsers.table.email"), "email"));
        columns.add(new PropertyColumn(createStringResource("PageUsers.table.role"), "role"));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        add(table);
    }

    private void userDetailsPerformed(AjaxRequestTarget target, User user) {
        PageParameters params = new PageParameters();
        params.set(PageUser.USER_ID, user.getId());

        setResponsePage(PageUser.class, params);
    }
}
