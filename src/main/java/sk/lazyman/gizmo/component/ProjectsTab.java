package sk.lazyman.gizmo.component;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.provider.BasicDataProvider;
import sk.lazyman.gizmo.web.PageTemplate;
import sk.lazyman.gizmo.web.app.PageProject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class ProjectsTab extends SimplePanel {

    private static final String ID_TABLE = "table";

    public ProjectsTab(String id) {
        super(id);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        initPanelLayout();
    }

    private void initPanelLayout() {
        PageTemplate page = (PageTemplate) getPage();
        BasicDataProvider provider = new BasicDataProvider(page.getProjectRepository());
        provider.setSort(new Sort(new Sort.Order(Sort.Direction.ASC, Project.F_NAME),
                new Sort.Order(Sort.Direction.DESC, Project.F_COMMERCIAL)));

        List<IColumn> columns = new ArrayList<>();

        columns.add(new LinkColumn<Project>(createStringResource("Project.name"), Project.F_NAME) {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<Project> rowModel) {
                projectDetailsPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new PropertyColumn(createStringResource("Project.customer"), Project.F_CUSTOMER + "." + Customer.F_NAME));
        columns.add(new PropertyColumn(createStringResource("Project.description"), Project.F_DESCRIPTION));
        columns.add(new PropertyColumn(createStringResource("Project.commercial"), Project.F_COMMERCIAL));
        columns.add(new PropertyColumn(createStringResource("Project.closed"), Project.F_CLOSED));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        add(table);
    }

    private void projectDetailsPerformed(AjaxRequestTarget target, Project customer) {

    }
}
