package sk.lazyman.gizmo.web.app;


import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.*;
import sk.lazyman.gizmo.component.data.DateColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.data.provider.SummaryDataProvider;
import sk.lazyman.gizmo.data.provider.SummaryPartsDataProvider;
import sk.lazyman.gizmo.data.provider.WorkDataProvider;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.util.ArrayList;
import java.util.Date;
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
    private static final String ID_SUMMARY = "summary";
    private static final String ID_SUMMARY_PARTS = "summaryParts";

    private IModel<WorkFilterDto> filter;

    public PageDashboard() {
        filter = new LoadableModel<WorkFilterDto>(false) {

            @Override
            protected WorkFilterDto load() {
                WorkFilterDto dto = new WorkFilterDto();
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

        form.add(new DateTextField(ID_FROM, new PropertyModel<Date>(filter, WorkFilterDto.F_FROM)));
        form.add(new DateTextField(ID_TO, new PropertyModel<Date>(filter, WorkFilterDto.F_TO)));

        form.add(new DropDownChoice<User>(ID_REALIZATOR, new PropertyModel<User>(filter, WorkFilterDto.F_REALIZATOR),
                GizmoUtils.createUserModel(getUserRepository()), new IChoiceRenderer<User>() {

            @Override
            public Object getDisplayValue(User object) {
                return object.getFullName();
            }

            @Override
            public String getIdValue(User object, int index) {
                return Integer.toString(index);
            }
        }) {

            @Override
            protected String getNullValidKey() {
                return "PageDashboard.realizator";
            }
        });

        initButtons(form);

        SummaryDataProvider summaryProvider = new SummaryDataProvider(this);
        SummaryPanel summary = new SummaryPanel(ID_SUMMARY, summaryProvider, filter);
        add(summary);

        SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(this);
        SummaryPartsPanel summaryParts = new SummaryPartsPanel(ID_SUMMARY_PARTS, partsProvider, filter);
        add(summaryParts);

        WorkDataProvider provider = new WorkDataProvider(getTaskRepository());
        provider.setFilter(filter.getObject());

        List<IColumn> columns = createColumns();
        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 50);
        table.setOutputMarkupId(true);
        add(table);
    }

    //date, length (invoice), realizator, project, description
    private List<IColumn> createColumns() {
        List<IColumn> columns = new ArrayList<>();

        columns.add(new DateColumn(createStringResource("Work.date"), Work.F_DATE, "EEE dd. MMM. yyyy"));
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
}
