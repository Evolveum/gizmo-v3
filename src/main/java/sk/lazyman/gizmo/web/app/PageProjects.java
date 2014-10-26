package sk.lazyman.gizmo.web.app;

import com.mysema.query.types.Predicate;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.BootstrapAjaxButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.Buttons;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.icon.FontAwesomeIconTypeBuilder;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.springframework.data.domain.Sort;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxButton;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.QCustomer;
import sk.lazyman.gizmo.data.QProject;
import sk.lazyman.gizmo.data.provider.BasicDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/projects")
public class PageProjects extends PageAppProjects {

    private static final String ID_TABLE = "table";
    private static final String ID_FORM = "form";
    private static final String ID_SEARCH_TEXT = "searchText";
    private static final String ID_SEARCH = "search";
    private static final String ID_CLEAR = "clear";
    private static final String ID_NEW_PROJECT = "newProject";

    private IModel<String> searchModel = new Model<>();

    public PageProjects() {
        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        TextField searchText = new TextField(ID_SEARCH_TEXT, searchModel);
        searchText.setOutputMarkupId(true);
        form.add(searchText);

        initButtons(form);

        BasicDataProvider provider = new BasicDataProvider(getProjectRepository()) {

            @Override
            public Predicate getPredicate() {
                String text = searchModel.getObject();
                if (StringUtils.isNotEmpty(text)) {
                    return QProject.project.name.lower().contains(text.toLowerCase())
                            .or(QProject.project.description.lower().contains(text.toLowerCase()));
                }

                return null;
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

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 20);
        table.setOutputMarkupId(true);
        add(table);
    }

    private void initButtons(Form form) {
        BootstrapAjaxButton search = new BootstrapAjaxButton(ID_SEARCH,
                createStringResource("PageProjects.search"), Buttons.Type.Primary) {

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                searchPerformed(target);
            }
        };
        search.setSize(Buttons.Size.Small);
        form.add(search);

        BootstrapAjaxButton clear = new BootstrapAjaxButton(ID_CLEAR, Buttons.Type.Danger) {

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                clearPerformed(target);
            }
        };
        clear.setSize(Buttons.Size.Small);
        clear.setIconType(
                FontAwesomeIconTypeBuilder.on(FontAwesomeIconTypeBuilder.FontAwesomeGraphic.trash_o)
                        .size(FontAwesomeIconTypeBuilder.Size.large).build());
        form.add(clear);

        AjaxButton newProject = new AjaxButton(ID_NEW_PROJECT, createStringResource("PageProjects.newProject")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newProjectPerformed(target);
            }
        };
        form.add(newProject);
    }

    private void newProjectPerformed(AjaxRequestTarget target) {
        setResponsePage(PageProject.class);
    }

    private void clearPerformed(AjaxRequestTarget target) {
        searchModel.setObject(null);
        target.add(get(ID_FORM + ":" + ID_SEARCH_TEXT), get(ID_TABLE));
    }

    private void searchPerformed(AjaxRequestTarget target) {
        target.add(get(ID_TABLE));
    }

    private void projectDetailsPerformed(AjaxRequestTarget target, Project customer) {
        PageParameters params = new PageParameters();
        params.set(PageProject.PROJECT_ID, customer.getId());

        setResponsePage(PageProject.class, params);
    }
}
