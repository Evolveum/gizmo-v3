package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.modal.DownloadTimeoffReportConfigPanel;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;

import java.util.ArrayList;
import java.util.List;

public class ReportEmployeeLogTab extends AbstractReportTab {

    private static final String ID_SUMMARY = "summary";
    private SummaryUsersPanel summaryPanel;

    public ReportEmployeeLogTab(String id) { super(id); }

    @Override protected boolean includeCustomerSearch() { return false; }

    @Override protected void buildResultsUI() {
        SummaryUserDataProvider provider = new SummaryUserDataProvider(getPageTemplate());
        summaryPanel = new SummaryUsersPanel(ID_SUMMARY, provider, getFilterModel());
        summaryPanel.setOutputMarkupId(true);
        addOrReplace(summaryPanel);
    }

    @Override protected void onPreview(AjaxRequestTarget target) {
        setFilterToSession(getFilterModel().getObject());
        targetAddFeedback(target);
        target.add(summaryPanel);
    }

    @Override protected void afterCalendarNavigation(AjaxRequestTarget target) {
        target.add(get(ID_FORM));
        target.add(summaryPanel);
    }

    @Override
    protected ReportFilterDto getFilterFromSession() {
        return GizmoAuthWebSession.getSession().getEmployeeReportFilterDto();
    }

    @Override
    protected void setFilterToSession(ReportFilterDto filter) {
        GizmoAuthWebSession.getSession().setEmployeeReportFilterDto(filter);
    }

    @Override protected Component createDownloadContent(String contentId) {
        return new DownloadTimeoffReportConfigPanel(contentId, getFilterModel());
    }

    @Override
    protected List<User> getDefaultRealizators() {
        return new ArrayList<>();
    }
}
