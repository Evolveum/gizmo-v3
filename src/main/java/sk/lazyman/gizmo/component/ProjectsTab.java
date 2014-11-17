package sk.lazyman.gizmo.component;

import com.mysema.query.types.Predicate;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.QProject;
import sk.lazyman.gizmo.data.provider.BasicDataProvider;
import sk.lazyman.gizmo.data.provider.CustomTabDataProvider;
import sk.lazyman.gizmo.repository.CustomerRepository;
import sk.lazyman.gizmo.web.app.PageAppTemplate;
import sk.lazyman.gizmo.web.app.PageCustomer;
import sk.lazyman.gizmo.web.app.PageProject;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class ProjectsTab extends SimplePanel {

    private static final String ID_TABLE = "table";
    private static final String ID_NEW_PROJECT = "newProject";

    public ProjectsTab(String id) {
        super(id);
    }

    @Override
    protected void onConfigure() {
        super.onConfigure();

        initPanelLayout();
    }

    private void initPanelLayout() {
        final PageCustomer page = (PageCustomer) getPage();
        BasicDataProvider provider = new CustomTabDataProvider(page.getProjectRepository()) {

            @Override
            public Predicate getPredicate() {
                Integer customerId = page.getIntegerParam(PageCustomer.CUSTOMER_ID);
                if (customerId == null) {
                    return null;
                }

                return QProject.project.customer.id.eq(customerId);
            }
        };
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

        AjaxButton newProject = new AjaxButton(ID_NEW_PROJECT, createStringResource("ProjectsTab.newProject")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newProjectPerformed(target);
            }
        };
        newProject.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                Integer customerId = page.getIntegerParam(PageCustomer.CUSTOMER_ID);
                return customerId != null;
            }
        });
        add(newProject);
    }

    private void newProjectPerformed(AjaxRequestTarget target) {
        PageAppTemplate page = (PageAppTemplate) getPage();
        try {
            Project project = new Project();

            Integer customerId = page.getIntegerParam(PageCustomer.CUSTOMER_ID);
            if (customerId != null) {
                CustomerRepository repository = page.getCustomerRepository();
                Customer customer = repository.findOne(customerId);
                project.setCustomer(customer);
            }

            PageProject next = new PageProject(null, new Model<>(project));
            setResponsePage(next);
        } catch (Exception ex) {
            page.handleGuiException(page, ex, target);
        }
    }

    private void projectDetailsPerformed(AjaxRequestTarget target, Project project) {
        PageParameters params = new PageParameters();
        params.set(PageProject.PROJECT_ID, project.getId());

        setResponsePage(PageProject.class, params);
    }
}
