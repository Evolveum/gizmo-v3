package sk.lazyman.gizmo.component;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.LoadableDetachableModel;
import org.apache.wicket.model.PropertyModel;
import sk.lazyman.gizmo.data.provider.SummaryPartsProvider;
import sk.lazyman.gizmo.dto.PartSummary;
import sk.lazyman.gizmo.dto.TaskFilterDto;

import java.text.DecimalFormat;
import java.util.List;

/**
 * @author lazyman
 */
public class SummaryPartsPanel extends SimplePanel<List<PartSummary>> {

    private static final String ID_PART_REPEATER = "partRepeater";
    private static final String ID_PART = "part";
    private static final String ID_WORK = "work";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_SUM_WORK = "sumWork";
    private static final String ID_SUM_INVOICE = "sumInvoice";

    public SummaryPartsPanel(String id, final SummaryPartsProvider provider, final IModel<TaskFilterDto> model) {
        super(id);

        setModel(new LoadableDetachableModel<List<PartSummary>>() {

            @Override
            protected List<PartSummary> load() {
                return provider.createSummary(model.getObject());
            }
        });

        setOutputMarkupId(true);
        initPanelLayout();
    }

    private void initPanelLayout() {
        ListView repeater = new ListView<PartSummary>(ID_PART_REPEATER, getModel()) {

            @Override
            protected void populateItem(final ListItem<PartSummary> item) {
                //todo fix full part name
                Label part = new Label(ID_PART, new PropertyModel<>(item.getModel(), PartSummary.F_NAME));
                part.setRenderBodyOnly(true);
                item.add(part);

                Label work = new Label(ID_WORK, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        PartSummary part = item.getModelObject();
                        Double hours = part.getLength();
                        return createLenght(hours);
                    }
                });
                work.setRenderBodyOnly(true);
                item.add(work);

                Label invoice = new Label(ID_INVOICE, new AbstractReadOnlyModel<String>() {

                    @Override
                    public String getObject() {
                        PartSummary part = item.getModelObject();
                        Double hours = part.getInvoice();
                        return createLenght(hours);
                    }
                });
                invoice.setRenderBodyOnly(true);
                item.add(invoice);
            }
        };
        add(repeater);

        Label sumWork = new Label(ID_SUM_WORK);
        sumWork.setRenderBodyOnly(true);
        add(sumWork);

        Label sumInvoice = new Label(ID_SUM_INVOICE);
        sumInvoice.setRenderBodyOnly(true);
        add(sumInvoice);
    }

    private String createLenght(Double hours) {
        if (hours == null) {
            hours = 0d;
        }
        Double days = hours / 24;

        DecimalFormat twoDForm = new DecimalFormat("#.##");
        return hours + " (" + twoDForm.format(days) + "d)";
    }
}
