/*
 *  Copyright (C) 2023 Evolveum
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package com.evolveum.gizmo.web.app;


import com.evolveum.gizmo.component.SummaryChartPanel;
import com.evolveum.gizmo.component.calendar.CalendarEventsProvider;
import com.evolveum.gizmo.component.calendar.CalendarPanel;
import com.evolveum.gizmo.component.data.MonthNavigationPanel;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.wicketstuff.annotation.mount.MountPath;

/**
 * @author lazyman
 */
@MountPath(value = "/dashboard", alt = "/app")
public class PageDashboard extends PageAppTemplate {

    private static final String ID_MONTH_NAVIGATION = "monthNavigation";
    private static final String ID_SUMMARY_PARTS = "summaryParts";

    private static final String ID_BTN_NEW_WORK = "newWork";
    private static final String ID_BTN_NEW_BULK = "newBulk";

    private static final String ID_CALENDAR = "calendar";

    private final IModel<ReportFilterDto> filter;

    public PageDashboard() {
        filter = new LoadableModel<>(false) {

            @Override
            protected ReportFilterDto load() {
                GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
                if (session.getDashboardFilter() != null) {
                    return session.getDashboardFilter();
                }
                ReportFilterDto dto = new ReportFilterDto();
                dto.setDateFrom(GizmoUtils.createWorkDefaultFrom());
                dto.setDateTo(GizmoUtils.createWorkDefaultTo());

                GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                dto.getRealizators().add(principal.getUser());

                session.setDashboardFilter(dto);
                return dto;
            }
        };

        initLayout();
    }

    private IModel<ReportFilterDto> getFilterModel() {
        return filter;
    }

    private void initLayout() {

        MonthNavigationPanel monthNavigation = new MonthNavigationPanel(ID_MONTH_NAVIGATION, getFilterModel()) {

            @Override
            protected void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
                PageDashboard.this.handleCalendarNavigation(target, workFilter);
            }

            @Override
            protected boolean isProgressPanelVisible() {
                return true;
            }
        };
        monthNavigation.setOutputMarkupId(true);
        add(monthNavigation);

        SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(this);
        SummaryChartPanel chart = new SummaryChartPanel(ID_SUMMARY_PARTS, partsProvider, getFilterModel());
        chart.setOutputMarkupId(true);
        add(chart);

        CalendarEventsProvider eventsProvider = new CalendarEventsProvider(PageDashboard.this, getFilterModel());
        CalendarPanel calendarPanel = new CalendarPanel(ID_CALENDAR, eventsProvider);
        add(calendarPanel);

    }

    @Override
    public Fragment createHeaderButtonsFragment(String fragmentId) {
        Fragment fragment = new  Fragment(fragmentId, "buttonsFragment", this);

        AjaxLink<String> newWork = new AjaxLink<>(ID_BTN_NEW_WORK, createStringResource("PageDashboard.newWork")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newWorkPerformed();
            }
        };
        newWork.setOutputMarkupId(true);
        fragment.add(newWork);

        AjaxLink<String> newBulk = new AjaxLink<>(ID_BTN_NEW_BULK, createStringResource("PageDashboard.newBulk")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newBulkPerformed();
            }
        };
        newBulk.setOutputMarkupId(true);
        fragment.add(newBulk);

        return fragment;
    }
    private void newWorkPerformed() {
        setResponsePage(PageWork.class);
    }

    private void newBulkPerformed() {
        setResponsePage(PageBulk.class);
    }

    private void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setDashboardFilter(workFilter);
        target.add(PageDashboard.this);
    }
}
