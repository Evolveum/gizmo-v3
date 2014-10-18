package sk.lazyman.gizmo.web.app;


import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.DropDownButton;
import de.agilecoders.wicket.core.markup.html.bootstrap.button.dropdown.MenuHeader;
import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AbstractAutoCompleteTextRenderer;
import org.apache.wicket.extensions.ajax.markup.html.autocomplete.AutoCompleteTextField;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.link.AbstractLink;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.Response;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.*;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.Customer;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.data.provider.SummaryDataProvider;
import sk.lazyman.gizmo.data.provider.SummaryPartsDataProvider;
import sk.lazyman.gizmo.data.provider.WorkDataProvider;
import sk.lazyman.gizmo.dto.DashboardProjectDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.repository.ProjectRepository;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

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
    private static final String ID_SUMMARY = "summary";
    private static final String ID_SUMMARY_PARTS = "summaryParts";

    private IModel<WorkFilterDto> filter;
    private IModel<List<DashboardProjectDto>> projects;

    public PageDashboard() {
        projects = new LoadableModel<List<DashboardProjectDto>>(false) {

            @Override
            protected List<DashboardProjectDto> load() {
                List<DashboardProjectDto> list = new ArrayList<>();

                ProjectRepository repository = getProjectRepository();
                List<Project> projects = repository.findOpenedProjects();
                Set<Customer> customers = new HashSet<>();

                for (Project project : projects) {
                    Customer customer = project.getCustomer();
                    String customerName = null;
                    if (customer != null) {
                        customers.add(customer);
                        customerName = customer.getName();
                    }

                    list.add(new DashboardProjectDto(customerName, project.getName(), project.getId()));
                }

                for (Customer customer : customers) {
                    list.add(new DashboardProjectDto(customer.getName(), customer.getId()));
                }

                return list;
            }
        };

        filter = new LoadableModel<WorkFilterDto>(false) {

            @Override
            protected WorkFilterDto load() {
                WorkFilterDto dto = new WorkFilterDto();
                dto.setFrom(GizmoUtils.createWorkDefaultFrom());
                dto.setTo(GizmoUtils.createWorkDefaultTo());

                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                dto.setRealizator(principal.getUser());
                return dto;
            }
        };

        initLayout();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new LessResourceReference(PageDashboard.class, "PageDashboard.less")));
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        form.add(new DateTextField(ID_FROM, new PropertyModel<Date>(filter, WorkFilterDto.F_FROM)));
        form.add(new DateTextField(ID_TO, new PropertyModel<Date>(filter, WorkFilterDto.F_TO)));

        form.add(new DropDownChoice<User>(ID_REALIZATOR, new PropertyModel<User>(filter, WorkFilterDto.F_REALIZATOR),
                GizmoUtils.createUserModel(getUserRepository()), GizmoUtils.createUserChoiceRenderer()) {

            @Override
            protected String getNullValidKey() {
                return "PageDashboard.realizator";
            }
        });

        AutoCompleteTextField project = new AutoCompleteTextField<DashboardProjectDto>(ID_PROJECT,
                new PropertyModel<DashboardProjectDto>(filter, WorkFilterDto.F_PROJECT),
                new AbstractAutoCompleteTextRenderer<DashboardProjectDto>() {

                    @Override
                    protected String getTextValue(DashboardProjectDto object) {
                        if (object == null) {
                            return null;
                        }

                        StringBuilder sb = new StringBuilder();
                        if (StringUtils.isNotEmpty(object.getCustomerName())) {
                            sb.append(object.getCustomerName());
                        }

                        if (StringUtils.isNotEmpty(object.getCustomerName())
                                && StringUtils.isNotEmpty(object.getProjectName())) {
                            sb.append(" / ");
                        }

                        if (StringUtils.isNotEmpty(object.getProjectName())) {
                            sb.append(object.getProjectName());
                        }

                        return sb.toString();
                    }
                }) {

            @Override
            protected Iterator<DashboardProjectDto> getChoices(String input) {
                List<DashboardProjectDto> list = projects.getObject();
                List<DashboardProjectDto> result = new ArrayList<>();
                for (DashboardProjectDto dto : list) {
                    if (dto.match(input)) {
                        result.add(dto);
                    }
                }

                return result.iterator();
            }
        };

        project.setLabel(createStringResource("PageDashboard.project"));
        form.add(project);

        initButtons(form);

        SummaryDataProvider summaryProvider = new SummaryDataProvider(this);
        SummaryPanel summary = new SummaryPanel(ID_SUMMARY, summaryProvider, filter);
        add(summary);

        SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(this);
        SummaryPartsPanel summaryParts = new SummaryPartsPanel(ID_SUMMARY_PARTS, partsProvider, filter);
        add(summaryParts);

        WorkDataProvider provider = new WorkDataProvider(getWorkRepository());
        provider.setFilter(filter.getObject());

        List<IColumn> columns = createColumns();
        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 50);
        table.setOutputMarkupId(true);
        add(table);
    }

    //date, length (invoice), realizator, project, description
    private List<IColumn> createColumns() {
        List<IColumn> columns = new ArrayList<>();

        columns.add(new LinkColumn<Work>(createStringResource("Work.date"), Work.F_DATE) {

            @Override
            protected IModel<String> createLinkModel(final IModel<Work> rowModel) {
                return new AbstractReadOnlyModel<String>() {
                    @Override
                    public String getObject() {
                        PropertyModel<Date> propertyModel = new PropertyModel<>(rowModel, getPropertyExpression());
                        Date date = propertyModel.getObject();
                        if (date == null) {
                            return null;
                        }

                        DateFormat df = new SimpleDateFormat("EEE dd. MMM. yyyy");
                        return df.format(date);
                    }
                };
            }

            @Override
            public void onClick(AjaxRequestTarget target, IModel<Work> rowModel) {
                workDetailsPerformed(target, rowModel.getObject());
            }
        });
        columns.add(new AbstractColumn<Work, String>(createStringResource("PageDashboard.length")) {

            @Override
            public void populateItem(Item<ICellPopulator<Work>> cellItem, String componentId, IModel<Work> rowModel) {
                cellItem.add(new Label(componentId, createInvoceModel(rowModel)));
            }
        });
        columns.add(new AbstractColumn<Work, String>(createStringResource("PageDashboard.realizator")) {

            @Override
            public void populateItem(Item<ICellPopulator<Work>> cellItem, String componentId, IModel<Work> rowModel) {
                cellItem.add(new Label(componentId, createRealizatorModel(rowModel)));
            }
        });
        columns.add(new AbstractColumn<Work, String>(createStringResource("PageDashboard.project")) {

            @Override
            public void populateItem(Item<ICellPopulator<Work>> cellItem, String componentId, IModel<Work> rowModel) {
                cellItem.add(new Label(componentId, createProjectModel(rowModel)));
            }
        });
        columns.add(new PropertyColumn(createStringResource("Work.description"), Work.F_DESCRIPTION));

        return columns;
    }

    private IModel<String> createProjectModel(final IModel<Work> rowModel) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Work work = rowModel.getObject();
                return GizmoUtils.describeProjectPart(work.getPart(), " ");
            }
        };
    }

    private IModel<String> createRealizatorModel(final IModel<Work> rowModel) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Work work = rowModel.getObject();
                return work.getRealizator().getFullName();
            }
        };
    }

    private IModel<String> createInvoceModel(final IModel<Work> rowModel) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Work work = rowModel.getObject();
                return StringUtils.join(new Object[]{work.getWorkLength(), " (", work.getInvoiceLength(), ')'});
            }
        };
    }

    private void initButtons(Form form) {
        AjaxSubmitButton display = new AjaxSubmitButton(ID_BTN_DISPLAY,
                createStringResource("PageDashboard.display")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                displayPerformed(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        };
        form.add(display);

        AjaxButton email = new AjaxButton(ID_BTN_EMAIL, createStringResource("PageDashboard.email")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                emailPerformed(target);
            }
        };
        form.add(email);

        AjaxButton print = new AjaxButton(ID_BTN_PRINT, createStringResource("PageDashboard.print")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                printPerformed(target);
            }
        };
        form.add(print);

        AjaxButton task = new AjaxButton(ID_BTN_NEW_TASK, createStringResource("PageDashboard.task")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newWorkPerformed(target);
            }
        };
        form.add(task);
    }

    private void displayPerformed(AjaxRequestTarget target) {
        TablePanel table = (TablePanel) get(ID_TABLE);
        WorkDataProvider provider = (WorkDataProvider) table.getDataTable().getDataProvider();
        provider.setFilter(filter.getObject());
        table.setCurrentPage(0L);

        target.add(get(ID_SUMMARY), get(ID_SUMMARY_PARTS), table);
    }

    private void emailPerformed(AjaxRequestTarget target) {

    }

    private void printPerformed(AjaxRequestTarget target) {

    }

    private void newWorkPerformed(AjaxRequestTarget target) {
        setResponsePage(PageWork.class);
    }

    private void workDetailsPerformed(AjaxRequestTarget target, Work work) {
        PageParameters params = new PageParameters();
        params.add(PageWork.WORK_ID, work.getId());

        setResponsePage(PageWork.class, params);
    }
}
