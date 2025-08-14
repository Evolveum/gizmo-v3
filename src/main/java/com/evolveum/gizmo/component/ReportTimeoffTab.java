package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.modal.DownloadTimeoffReportConfigPanel;
import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

public class ReportTimeoffTab extends AbstractReportTab {

    private static final String ID_SUMMARY = "summary";
    private SummaryUsersPanel summaryPanel;

    public ReportTimeoffTab(String id) { super(id); }

    @Override protected boolean includeCustomerSearch() { return false; } 

    @Override protected void buildResultsUI() {
        SummaryUserDataProvider provider = new SummaryUserDataProvider(getPageTemplate());
        summaryPanel = new SummaryUsersPanel(ID_SUMMARY, provider, getFilterModel());
        summaryPanel.setOutputMarkupId(true);
        addOrReplace(summaryPanel);
    }

    @Override protected void onPreview(AjaxRequestTarget target) {
        GizmoAuthWebSession.getSession().setReportFilterDto(getFilterModel().getObject());
        targetAddFeedback(target);
        target.add(summaryPanel);
    }

    @Override protected void afterCalendarNavigation(AjaxRequestTarget target) {
        target.add(form);
        target.add(summaryPanel);
    }

    @Override protected Component createDownloadContent(String contentId) {
        return new DownloadTimeoffReportConfigPanel(contentId, getFilterModel());
    }
}
