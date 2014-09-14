package sk.lazyman.gizmo.web.app;


import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.ISortableDataProvider;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.IModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.DateColumn;
import sk.lazyman.gizmo.component.TablePanel;
import sk.lazyman.gizmo.data.Task;
import sk.lazyman.gizmo.data.provider.TaskDataProvider;
import sk.lazyman.gizmo.dto.TaskFilterDto;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath(value = "/app/dashboard", alt = "/app")
public class PageDashboard extends PageAppTemplate {

    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_TABLE = "table";
    private static final String ID_PROJECT = "project";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_BTN_DISPLAY = "display";
    private static final String ID_BTN_EMAIL = "email";
    private static final String ID_BTN_PRINT = "print";
    private static final String ID_BTN_NEW_TASK = "task";

    private IModel<TaskFilterDto> filter;

    public PageDashboard() {
        filter = new LoadableModel<TaskFilterDto>() {

            @Override
            protected TaskFilterDto load() {
                TaskFilterDto dto = new TaskFilterDto();
                dto.setFrom(GizmoUtils.createTaskDefaultFrom());
                dto.setTo(GizmoUtils.createTaskDefaultTo());

                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                dto.setRealizator(principal.getUser());
                return dto;
            }
        };

        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);


        form.add(new DateTextField(ID_FROM));
        form.add(new DateTextField(ID_TO));

        TaskDataProvider provider = new TaskDataProvider(getTaskRepository());
        provider.setFilter(filter.getObject());

        List<IColumn> columns = createColumns();
        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 50);
        add(table);
    }

    //date, length (invoice), realizator, project, description
    private List<IColumn> createColumns() {
        List<IColumn> columns = new ArrayList<>();

        columns.add(new DateColumn(createStringResource("Task.date"), Task.F_DATE, "EEE dd. MMM. yyyy"));
        columns.add(new AbstractColumn<Task, String>(createStringResource("PageDashboard.length")) {

            @Override
            public void populateItem(Item<ICellPopulator<Task>> cellItem, String componentId, IModel<Task> rowModel) {
                cellItem.add(new Label(componentId));
            }
        });
        columns.add(new PropertyColumn(createStringResource("Task.desc"), Task.F_DESC));

        return columns;
    }
}
