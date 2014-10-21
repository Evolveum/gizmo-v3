package sk.lazyman.gizmo.component;

import com.mysema.query.types.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.*;
import sk.lazyman.gizmo.data.provider.BasicDataProvider;
import sk.lazyman.gizmo.data.provider.CustomTabDataProvider;
import sk.lazyman.gizmo.web.app.PageCustomer;
import sk.lazyman.gizmo.web.app.PageProject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class ProjectPartsTab extends SimplePanel {

    private static final String ID_TABLE = "table";
    private static final String ID_NEW_PART = "newPart";

    public ProjectPartsTab(String id) {
        super(id);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        initPanelLayout();
    }

    private void initPanelLayout() {
        AjaxButton newProject = new AjaxButton(ID_NEW_PART, createStringResource("ProjectPartsTab.newPart")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newPartPerformed(target);
            }
        };
        add(newProject);

        final PageProject page = (PageProject) getPage();
        BasicDataProvider provider = new CustomTabDataProvider(page.getProjectPartRepository()) {

            @Override
            public Predicate getPredicate() {
                Integer projectId = page.getIntegerParam(PageProject.PROJECT_ID);
                if (projectId == null) {
                    return null;
                }

                return QPart.part.project.id.eq(projectId);
            }
        };
        provider.setSort(new Sort(new Sort.Order(Sort.Direction.ASC, Project.F_NAME)));

        List<IColumn> columns = new ArrayList<>();
        columns.add(new LinkColumn<Part>(createStringResource("Part.name"), Part.F_NAME) {

            @Override
            public void onClick(AjaxRequestTarget target, IModel<Part> rowModel) {
                editPartPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new PropertyColumn(createStringResource("Part.description"), Part.F_DESCRIPTION));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        add(table);
    }

    protected void newPartPerformed(AjaxRequestTarget target) {

    }

    protected void editPartPerformed(AjaxRequestTarget target, Part part) {

    }
}
