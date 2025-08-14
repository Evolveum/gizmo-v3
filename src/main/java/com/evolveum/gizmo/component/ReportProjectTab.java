package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.modal.DownloadProjectReportConfigPanel;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class ReportProjectTab extends AbstractReportTab {

    private static final String ID_SUMMARY = "summary";
    private SummaryPartsPanel summaryPanel;

    public ReportProjectTab(String id) { super(id); }

    @Override protected boolean includeCustomerSearch() { return true; }

    @Override protected void buildResultsUI() {
        SummaryPartsDataProvider provider = new SummaryPartsDataProvider(getPageTemplate());
        summaryPanel = new SummaryPartsPanel(ID_SUMMARY, provider, getFilterModel());
        summaryPanel.setOutputMarkupId(true);
        addOrReplace(summaryPanel);
    }

    @Override protected void onPreview(AjaxRequestTarget target) {
        ReportFilterDto rf = getFilterModel().getObject();
        GizmoAuthWebSession.getSession().setReportFilterDto(rf);
        targetAddFeedback(target);
        target.add(summaryPanel);
    }

    @Override protected void afterCalendarNavigation(AjaxRequestTarget target) {
        target.add(form);
        target.add(summaryPanel);
    }

    @Override protected Component createDownloadContent(String contentId) {
        return new DownloadProjectReportConfigPanel(contentId, getFilterModel());
    }
}
