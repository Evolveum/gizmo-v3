package sk.lazyman.gizmo.web.app;

import de.agilecoders.wicket.less.LessResourceReference;
import org.apache.wicket.markup.head.CssHeaderItem;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.list.ListItem;
import org.apache.wicket.markup.html.list.ListView;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.PartAutoCompleteConverter;
import sk.lazyman.gizmo.dto.CustomerProjectPartDto;
import sk.lazyman.gizmo.dto.WorkFilterDto;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author lazyman
 */
@MountPath("/app/print")
public class PagePrint extends PageAppTemplate {

    private static final String ID_PROJECT = "project";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_INVOICE = "invoice";
    private static final String ID_WORK = "work";
    private static final String ID_DATA = "data";
    private static final String ID_DATE = "date";
    private static final String ID_LENGTH = "length";
    private static final String ID_PROJECT_PART = "projectPart";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_DESCRIPTION = "description";

    private IModel<WorkFilterDto> filter;

    public PagePrint() {
        this(null);
    }

    public PagePrint(IModel<WorkFilterDto> filter) {
        this.filter = filter != null ? filter : new Model<>(new WorkFilterDto());

        initLayout();
    }

    @Override
    public void renderHead(IHeaderResponse response) {
        super.renderHead(response);
        response.render(CssHeaderItem.forReference(new LessResourceReference(PagePrint.class, "PagePrint.less")));
    }

    private void initLayout() {
        Label project = new Label(ID_PROJECT, createProjectModel());
        add(project);

        Label from = new Label(ID_FROM, createStringDateModel(new PropertyModel<Date>(filter, WorkFilterDto.F_FROM)));
        add(from);

        Label to = new Label(ID_TO, createStringDateModel(new PropertyModel<Date>(filter, WorkFilterDto.F_TO)));
        add(to);

        Label invoice = new Label(ID_INVOICE);
        invoice.setRenderBodyOnly(true);
        add(invoice);

        Label work = new Label(ID_WORK);
        work.setRenderBodyOnly(true);
        add(work);

        ListView data = new ListView(ID_DATA) {

            @Override
            protected void populateItem(ListItem item) {
                initItem(item);
            }
        };
        add(data);
    }

    private void initItem(ListItem item) {
        Label date = new Label(ID_DATE);
        item.add(date);

        Label length = new Label(ID_LENGTH);
        item.add(length);

        Label projectPart = new Label(ID_PROJECT_PART);
        item.add(projectPart);

        Label realizator = new Label(ID_REALIZATOR);
        item.add(realizator);

        Label description = new Label(ID_DESCRIPTION);
        item.add(description);
    }

    private IModel<String> createHourMDModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                return null;
            }
        };
    }

    private IModel<String> createProjectModel() {
        return new AbstractReadOnlyModel<String>() {

            @Override
            public String getObject() {
                WorkFilterDto dto = filter.getObject();
                CustomerProjectPartDto projectDto = dto.getProject();

                return PartAutoCompleteConverter.convertToString(projectDto);
            }
        };
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
}
