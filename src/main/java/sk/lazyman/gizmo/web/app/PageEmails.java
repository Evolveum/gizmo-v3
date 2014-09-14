package sk.lazyman.gizmo.web.app;

import de.agilecoders.wicket.extensions.markup.html.bootstrap.form.DateTextField;
import org.apache.commons.lang.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.basic.MultiLineLabel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.IChoiceRenderer;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.springframework.data.domain.Sort;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.AjaxSubmitButton;
import sk.lazyman.gizmo.component.DateColumn;
import sk.lazyman.gizmo.component.IconColumn;
import sk.lazyman.gizmo.component.TablePanel;
import sk.lazyman.gizmo.data.EmailLog;
import sk.lazyman.gizmo.data.Project;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.data.provider.EmailDataProvider;
import sk.lazyman.gizmo.dto.EmailFilterDto;
import sk.lazyman.gizmo.repository.UserRepository;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.util.*;

/**
 * @author lazyman
 */
@MountPath("/app/emails")
public class PageEmails extends PageAppTemplate {

    private static final String ID_TABLE = "table";
    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_SENDER = "sender";
    private static final String ID_FILTER = "filter";

    private IModel<EmailFilterDto> filter;

    public PageEmails() {
        filter = new LoadableModel<EmailFilterDto>(false) {

            @Override
            protected EmailFilterDto load() {
                return new EmailFilterDto();
            }
        };

        initLayout();
    }

    private void initLayout() {
        Form form = new Form(ID_FORM);
        add(form);

        form.add(new DateTextField(ID_FROM, new PropertyModel<Date>(filter, EmailFilterDto.F_FROM)));
        form.add(new DateTextField(ID_TO, new PropertyModel<Date>(filter, EmailFilterDto.F_TO)));

        form.add(new DropDownChoice<User>(ID_SENDER, new PropertyModel<User>(filter, EmailFilterDto.F_SENDER),
                createSenderModel(), new IChoiceRenderer<User>() {

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
                return "PageEmails.sender";
            }
        });

        form.add(new AjaxSubmitButton(ID_FILTER, createStringResource("PageEmails.filter")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                filterLogs(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target, Form<?> form) {
                target.add(getFeedbackPanel());
            }
        });

        SortableDataProvider provider = new EmailDataProvider(getEmailLogRepository());
        List<IColumn> columns = new ArrayList<>();

        columns.add(new DateColumn(createStringResource("EmailLog.date"), EmailLog.F_DATE, "dd. MMM, yyyy HH:mm:ss"));
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.sender")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> item, String componentId,
                                     final IModel<EmailLog> rowModel) {
                item.add(new Label(componentId, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        EmailLog log = rowModel.getObject();
                        return log.getSender().getFullName();
                    }
                }));
            }
        });
        columns.add(new IconColumn<EmailLog>(createStringResource("EmailLog.successful")) {

            @Override
            protected IModel<String> createTitleModel(IModel<EmailLog> rowModel) {
                EmailLog log = rowModel.getObject();
                String key = log.isSuccessful() ? "PageEmails.success" : "PageEmails.failure";
                return createStringResource(key);
            }

            @Override
            protected IModel<String> createIconModel(final IModel<EmailLog> rowModel) {
                return new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        EmailLog log = rowModel.getObject();
                        return "fa fa-fw " +
                                (log.isSuccessful() ? "fa-check-circle text-success" : "fa-times-circle text-danger");
                    }
                };
            }
        });
        columns.add(new PropertyColumn(createStringResource("EmailLog.mailTo"), EmailLog.F_MAIL_TO));
        columns.add(new DateColumn(createStringResource("EmailLog.from"), EmailLog.F_FROM, "dd. MMM, yyyy"));
        columns.add(new DateColumn(createStringResource("EmailLog.to"), EmailLog.F_TO, "dd. MMM, yyyy"));
        columns.add(new PropertyColumn(createStringResource("EmailLog.summaryWork"), EmailLog.F_SUMMARY_WORK));
        columns.add(new PropertyColumn(createStringResource("EmailLog.summaryInvoice"), EmailLog.F_SUMMARY_INVOICE));
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.realizators")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> cellItem, String componentId,
                                     IModel<EmailLog> rowModel) {

                MultiLineLabel label = new MultiLineLabel(componentId, createRealizators(rowModel));
                cellItem.add(label);
            }
        });
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.projects")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> cellItem, String componentId,
                                     final IModel<EmailLog> rowModel) {
                MultiLineLabel label = new MultiLineLabel(componentId, createProjects(rowModel));
                cellItem.add(label);
            }
        });

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        table.setOutputMarkupId(true);
        add(table);
    }

    private IModel<String> createRealizators(final IModel<EmailLog> rowModel) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                EmailLog log = rowModel.getObject();
                Set<User> set = log.getRealizatorList();
                if (set == null) {
                    return null;
                }

                List<String> names = new ArrayList<>();
                for (User user : set) {
                    names.add(user.getFullName());
                }
                Collections.sort(names);

                return StringUtils.join(names, '\n');
            }
        };
    }

    private IModel<String> createProjects(final IModel<EmailLog> rowModel) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                EmailLog log = rowModel.getObject();
                Set<Project> set = log.getProjectList();
                if (set == null) {
                    return null;
                }

                List<String> names = new ArrayList<>();
                for (Project project : set) {
                    names.add(GizmoUtils.describeProject(project));
                }
                Collections.sort(names);

                return StringUtils.join(names, '\n');
            }
        };
    }

    private IModel<List<User>> createSenderModel() {
        return new LoadableModel<List<User>>(false) {

            @Override
            protected List<User> load() {
                UserRepository repo = getUserRepository();
                return repo.findAll(new Sort(Sort.Direction.ASC, User.F_FIRST_NAME, User.F_LAST_NAME));
            }
        };
    }

    private void filterLogs(AjaxRequestTarget target) {
        TablePanel table = (TablePanel) get(ID_TABLE);
        EmailDataProvider provider = (EmailDataProvider) table.getDataTable().getDataProvider();
        provider.setFilter(filter.getObject());
        table.setCurrentPage(0L);

        target.add(table);
    }
}
