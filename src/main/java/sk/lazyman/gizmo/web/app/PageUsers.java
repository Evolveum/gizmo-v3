package sk.lazyman.gizmo.web.app;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.data.provider.BasicDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/users")
public class PageUsers extends PageAppUsers {

    private static final String ID_TABLE = "table";

    public PageUsers() {
        initLayout();
    }

    private void initLayout() {
        BasicDataProvider provider = new BasicDataProvider(getUserRepository(), 15);
        provider.setSort(new Sort(Sort.Direction.ASC, User.F_NAME));

        List<IColumn> columns = new ArrayList<>();
        columns.add(new LinkColumn<User>(createStringResource("User.name"), User.F_NAME) {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<User> rowModel) {
                userDetailsPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new PropertyColumn(createStringResource("User.givenName"), User.F_GIVEN_NAME));
        columns.add(new PropertyColumn(createStringResource("User.familyName"), User.F_FAMILY_NAME));
        columns.add(new PropertyColumn(createStringResource("User.ldapDn"), User.F_LDAP_DN));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        add(table);
    }

    private void userDetailsPerformed(AjaxRequestTarget target, User user) {
        PageParameters params = new PageParameters();
        params.set(PageUser.USER_ID, user.getId());

        setResponsePage(PageUser.class, params);
    }
}
