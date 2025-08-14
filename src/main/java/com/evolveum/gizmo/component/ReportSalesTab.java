package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.component.modal.DownloadSalesReportConfigPanel;
import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.WorkDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.util.GizmoUtils;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;

import java.util.ArrayList;
import java.util.List;

public class ReportSalesTab extends AbstractReportTab {

    private static final String ID_TABLE = "table";

    public ReportSalesTab(String id) { super(id); }

    @Override protected boolean includeCustomerSearch() { return true; }

    @Override protected void buildResultsUI() {
        ReportDataProvider provider = new ReportDataProvider(getPageTemplate());
        provider.setFilter(getFilterModel().getObject());

        List<IColumn<WorkDto, String>> columns = new ArrayList<>();
        columns.add(new PropertyColumn<>(createStringResource("AbstractTask.date"), AbstractTask.F_DATE));
        columns.add(GizmoUtils.createWorkInvoiceColumn(getPageTemplate()));
        columns.add(GizmoUtils.createAbstractTaskRealizatorColumn(getPageTemplate()));
        columns.add(GizmoUtils.createWorkProjectColumn(getPageTemplate()));
        columns.add(new PropertyColumn<>(createStringResource("AbstractTask.description"), AbstractTask.F_DESCRIPTION));

        TablePanel<WorkDto> table = new TablePanel<>(ID_TABLE, provider, columns, 50);
        table.setOutputMarkupId(true);
        addOrReplace(table);
    }

    @Override protected void onPreview(AjaxRequestTarget target) {
        GizmoAuthWebSession.getSession().setReportFilterDto(getFilterModel().getObject());
        targetAddFeedback(target);
        target.add(form);
        target.add(get(ID_TABLE));
    }

    @Override protected void afterCalendarNavigation(AjaxRequestTarget target) {
        target.add(form);
        target.add(get(ID_TABLE));
    }

    @Override protected Component createDownloadContent(String contentId) {
        return new DownloadSalesReportConfigPanel(contentId, getFilterModel());
    }

    @Override protected void beforeOpenDownload(AjaxRequestTarget target, Component content) {
        if (content instanceof DownloadSalesReportConfigPanel c) {
            c.syncReportNameWithFilter(target);
        }
    }
}
