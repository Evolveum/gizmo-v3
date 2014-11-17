package sk.lazyman.gizmo.component;

import org.apache.commons.lang.StringUtils;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import sk.lazyman.gizmo.data.AbstractTask;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * @author lazyman
 */
public class ReportSearchSummary extends SimplePanel<WorkFilterDto> {

    private static final String ID_PROJECT = "project";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_WORK = "work";

    private IModel<List<AbstractTask>> dataModel;

    public ReportSearchSummary(String id, IModel<WorkFilterDto> model, IModel<List<AbstractTask>> dataModel) {
        super(id, model);
        setRenderBodyOnly(true);

        this.dataModel = dataModel;

        initPanelLayout();
    }

    private void initPanelLayout() {
        Label project = new Label(ID_PROJECT, createProjectModel());
        add(project);

        Label from = new Label(ID_FROM, createStringDateModel(new PropertyModel<Date>(getModel(), WorkFilterDto.F_FROM)));
        add(from);

        Label to = new Label(ID_TO, createStringDateModel(new PropertyModel<Date>(getModel(), WorkFilterDto.F_TO)));
        add(to);

        Label invoice = new Label(ID_INVOICE, createInvoiceModel(dataModel));
        invoice.setRenderBodyOnly(true);
        add(invoice);

        Label work = new Label(ID_WORK, createWorkModel(dataModel));
        work.setRenderBodyOnly(true);
        add(work);
    }

    private IModel<String> createStringDateModel(final IModel<Date> dateModel) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                Date date = dateModel.getObject();
                if (date == null) {
                    return null;
                }

                DateFormat df = new SimpleDateFormat("EEE dd. MMM. yyyy");
                return df.format(date);
            }
        };
    }

    private IModel<String> createInvoiceModel(final IModel<List<AbstractTask>> data) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                List<AbstractTask> list = data.getObject();
                double sum = 0;

                for (AbstractTask task : list) {
                    if (task instanceof Work) {
                        sum += ((Work) task).getInvoiceLength();
                    }
                }

                return createHourMd(sum);
            }
        };
    }

    private IModel<String> createWorkModel(final IModel<List<AbstractTask>> data) {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                List<AbstractTask> list = data.getObject();
                double sum = 0;

                for (AbstractTask task : list) {
                    sum += task.getWorkLength();
                }

                return createHourMd(sum);
            }
        };
    }

    private String createHourMd(double hours) {
        return StringUtils.join(new Object[]{hours, "/", hours / 8});
    }

    private IModel<String> createProjectModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                WorkFilterDto dto = getModel().getObject();
                CustomerProjectPartDto projectDto = dto.getProject();

                return PartAutoCompleteConverter.convertToString(projectDto);
            }
        };
    }
}
