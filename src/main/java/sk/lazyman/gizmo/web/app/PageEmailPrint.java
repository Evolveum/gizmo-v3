package sk.lazyman.gizmo.web.app;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.spring.injection.annot.SpringBean;
import sk.lazyman.gizmo.component.DataPrintPanel;
import sk.lazyman.gizmo.dto.WorkFilterDto;

import javax.persistence.EntityManager;

/**
 * @author lazyman
 */
public class PageEmailPrint extends WebPage {

    private static final String ID_DATA_PRINT = "dataPrint";

    @SpringBean
    private EntityManager entityManager;

    public PageEmailPrint(IModel<WorkFilterDto> filter) {
        initLayout(filter != null ? filter : new Model<>(new WorkFilterDto()));
    }

    private void initLayout(IModel<WorkFilterDto> filter) {
        DataPrintPanel dataPrint = new DataPrintPanel(ID_DATA_PRINT, filter, entityManager);
        add(dataPrint);
    }
}
