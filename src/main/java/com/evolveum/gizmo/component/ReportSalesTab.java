package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.data.WorkDataTable;
import com.evolveum.gizmo.component.modal.DownloadSalesReportConfigPanel;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class ReportSalesTab extends AbstractReportTab {

    private static final String ID_TABLE = "table";

    public ReportSalesTab(String id) { super(id); }

    @Override protected void buildResultsUI() {
        WorkDataTable table = new WorkDataTable(ID_TABLE, getFilterModel(), false);
        table.setOutputMarkupId(true);
        add(table);
    }

    @Override protected void onPreview(AjaxRequestTarget target) {
        GizmoAuthWebSession.getSession().setReportFilterDto(getFilterModel().getObject());
        targetAddFeedback(target);
        target.add(get(ID_FORM));
        target.add(get(ID_TABLE));
    }

    @Override protected void afterCalendarNavigation(AjaxRequestTarget target) {
        target.add(get(ID_FORM));
        target.add(get(ID_TABLE));
    }

    @Override
    protected ReportFilterDto getFilterFromSession() {
        return GizmoAuthWebSession.getSession().getCustomerReportFilterDto();
    }

    @Override
    protected void setFilterToSession(ReportFilterDto filter) {
        GizmoAuthWebSession.getSession().setCustomerReportFilterDto(filter);
    }

    @Override protected Component createDownloadContent(String contentId) {
        return new DownloadSalesReportConfigPanel(contentId, getFilterModel());
    }

}
