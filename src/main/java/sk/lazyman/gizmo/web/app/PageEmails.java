package sk.lazyman.gizmo.web.app;

import org.apache.wicket.extensions.markup.html.repeater.data.grid.ICellPopulator;
import org.apache.wicket.extensions.markup.html.repeater.data.table.AbstractColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.DateColumn;
import sk.lazyman.gizmo.component.IconColumn;
import sk.lazyman.gizmo.component.TablePanel;
import sk.lazyman.gizmo.data.EmailLog;
import sk.lazyman.gizmo.data.provider.EmailDataProvider;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/emails")
public class PageEmails extends PageAppTemplate {

    private static final String ID_TABLE = "table";

    public PageEmails() {
        initLayout();
    }

    private void initLayout() {
        SortableDataProvider provider = new EmailDataProvider(getEmailLogRepository());
        List<IColumn> columns = new ArrayList<>();

        //todo add search from/to date and sender

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
                                     final IModel<EmailLog> rowModel) {
                cellItem.add(new Label(componentId));
            }
        });
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.projects")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> cellItem, String componentId,
                                     final IModel<EmailLog> rowModel) {
                cellItem.add(new Label(componentId));
            }
        });

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        add(table);
    }
}
