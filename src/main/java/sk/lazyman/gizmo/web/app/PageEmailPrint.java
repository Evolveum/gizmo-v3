package sk.lazyman.gizmo.web.app;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import sk.lazyman.gizmo.component.DataPrintPanel;
import sk.lazyman.gizmo.data.AbstractTask;
import sk.lazyman.gizmo.dto.WorkFilterDto;

import java.util.List;

/**
 * @author lazyman
 */
public class PageEmailPrint extends WebPage {

    private static final String ID_DATA_PRINT = "dataPrint";

    public PageEmailPrint(IModel<WorkFilterDto> filter, IModel<List<AbstractTask>> dataModel) {
        initLayout(filter != null ? filter : new Model<>(new WorkFilterDto()), dataModel);
    }

    private void initLayout(IModel<WorkFilterDto> filter, IModel<List<AbstractTask>> dataModel) {
        DataPrintPanel dataPrint = new DataPrintPanel(ID_DATA_PRINT, filter, dataModel);
        add(dataPrint);
    }
}
