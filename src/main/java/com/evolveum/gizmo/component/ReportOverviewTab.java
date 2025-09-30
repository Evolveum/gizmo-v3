package com.evolveum.gizmo.component;

import com.evolveum.gizmo.component.calendar.CalendarEventsProvider;
import com.evolveum.gizmo.component.calendar.CalendarPanel;
import com.evolveum.gizmo.component.data.WorkDataTable;
import com.evolveum.gizmo.component.modal.DownloadOverviewReportConfigPanel;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import org.apache.wicket.Component;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.markup.html.WebMarkupContainer;

import java.util.ArrayList;
import java.util.List;

public class ReportOverviewTab extends AbstractReportTab {

    private static final String ID_OVERVIEW_TABS = "overviewTabs";

    public ReportOverviewTab(String id) { super(id); }

    @Override protected void buildResultsUI() {

        GizmoTabbedPanel<ITab> tabbedPanel = new GizmoTabbedPanel<>(ID_OVERVIEW_TABS, createTabs());
        add(tabbedPanel);

    }

    private List<ITab> createTabs() {
        List<ITab> tabList = new ArrayList<>();

        tabList.add(new AbstractTab(createStringResource("ReportOverviewTab.tab.calendar")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                CalendarEventsProvider eventsProvider = new CalendarEventsProvider(getPageTemplate(), getModel());
                CalendarPanel calendarPanel = new CalendarPanel(panelId, eventsProvider);
                calendarPanel.setOutputMarkupId(true);
                return calendarPanel;
            }
        });

        tabList.add(new AbstractTab(createStringResource("ReportOverviewTab.tab.details")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {

                WorkDataTable table = new WorkDataTable(panelId, getModel(), false);
                table.setOutputMarkupId(true);
                return table;
            }
        });

        tabList.add(new AbstractTab(createStringResource("ReportOverviewTab.tab.summary")) {

            @Override
            public WebMarkupContainer getPanel(String panelId) {
                SummaryPartsDataProvider provider = new SummaryPartsDataProvider(getPageTemplate());
                SummaryPartsPanel summaryPanel = new SummaryPartsPanel(panelId, provider, getModel());
                summaryPanel.setOutputMarkupId(true);
                return summaryPanel;
            }
        });

        return tabList;
    }

    @Override protected void onPreview(AjaxRequestTarget target) {
        GizmoAuthWebSession.getSession().setReportFilterDto(getModel().getObject());
        targetAddFeedback(target);
        target.add(get(ID_OVERVIEW_TABS));
    }

    @Override protected void afterCalendarNavigation(AjaxRequestTarget target) {
        target.add(get(ID_OVERVIEW_TABS));
    }

    @Override
    protected ReportFilterDto getFilterFromSession() {
        return GizmoAuthWebSession.getSession().getOverviewReportFilterDto();
    }

    @Override
    protected void setFilterToSession(ReportFilterDto filter) {
        GizmoAuthWebSession.getSession().setOverviewReportFilterDto(filter);
    }

    @Override protected Component createDownloadContent() {
        return new DownloadOverviewReportConfigPanel(org.apache.wicket.extensions.ajax.markup.html.modal.ModalDialog.CONTENT_ID, getModel());
    }

}
