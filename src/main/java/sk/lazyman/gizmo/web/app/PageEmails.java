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
import sk.lazyman.gizmo.component.IconColumn;
import sk.lazyman.gizmo.component.TablePanel;
import sk.lazyman.gizmo.data.EmailLog;
import sk.lazyman.gizmo.data.User;
import sk.lazyman.gizmo.data.provider.EmailDataProvider;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

        //add search from/to date and sender

        //date & time,sender,status,to,from date,to date,work,invoice,realizator list,project list
        columns.add(new AbstractColumn<EmailLog, String>(createStringResource("EmailLog.date")) {

            @Override
            public void populateItem(Item<ICellPopulator<EmailLog>> item, String componentId, IModel<EmailLog> rowModel) {
                item.add(new Label(componentId, createDateTimeModel(rowModel)));
            }
        });
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
                return super.createTitleModel(rowModel);
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


//        columns.add(new PropertyColumn(createStringResource("EmailLog."), EmailLog.d));
//        columns.add(new PropertyColumn(createStringResource("EmailLog."), ));
//        columns.add(new PropertyColumn(createStringResource("EmailLog."), ));
//        columns.add(new PropertyColumn(createStringResource("EmailLog."), ));
//        columns.add(new PropertyColumn(createStringResource("EmailLog."), ));

        TablePanel table = new TablePanel(ID_TABLE, provider, columns, 15);
        add(table);
    }

    private IModel<String> createDateTimeModel(final IModel<EmailLog> rowModel) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                EmailLog log = rowModel.getObject();
                Date date = log.getDate();
                if (date == null) {
                    return null;
                }

                DateFormat df = new SimpleDateFormat("HH:mm:ss dd. MMM, yyyy");
                return df.format(date);
            }
        };
    }
}
