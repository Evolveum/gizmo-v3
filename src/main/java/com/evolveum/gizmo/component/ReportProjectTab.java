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

    @Override protected void buildResultsUI() {
        SummaryPartsDataProvider provider = new SummaryPartsDataProvider(getPageTemplate());
        summaryPanel = new SummaryPartsPanel(ID_SUMMARY, provider, getModel());
        summaryPanel.setOutputMarkupId(true);
        add(summaryPanel);
    }

    @Override protected void onPreview(AjaxRequestTarget target) {
        ReportFilterDto rf = getModel().getObject();
        GizmoAuthWebSession.getSession().setReportFilterDto(rf);
        targetAddFeedback(target);
        target.add(summaryPanel);
    }

    @Override protected void afterCalendarNavigation(AjaxRequestTarget target) {
        target.add(get(ID_FORM));
        target.add(summaryPanel);
    }

    @Override
    protected ReportFilterDto getFilterFromSession() {
        return GizmoAuthWebSession.getSession().getProjectReportFilterDto();
    }

    @Override
    protected void setFilterToSession(ReportFilterDto filter) {
        GizmoAuthWebSession.getSession().setProjectReportFilterDto(filter);
    }

    @Override protected Component createDownloadContent() {
        return new DownloadProjectReportConfigPanel(org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog.CONTENT_ID, getModel());
    }
}
