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


import com.evolveum.gizmo.component.data.MonthNavigationPanel;
import com.evolveum.gizmo.component.data.WorkDataTable;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
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
@MountPath("/app/workReport")
public class PageWorkReport extends PageAppTemplate {

    private static final String ID_MONTH_NAVIGATION = "monthNavigation";
    private static final String ID_TABLE = "table";

    private static final String ID_BTN_NEW_WORK = "newWork";
    private static final String ID_BTN_NEW_BULK = "newBulk";

    private final IModel<ReportFilterDto> filter;

    public PageWorkReport() {
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

    private void initLayout() {

        MonthNavigationPanel monthNavigationPanel = new MonthNavigationPanel(ID_MONTH_NAVIGATION, filter) {

            @Override
            protected void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
                PageWorkReport.this.handleCalendarNavigation(target, workFilter);
            }

            @Override
            protected boolean isProgressPanelVisible() {
                return true;
            }

            @Override
            protected boolean isCustomDateRangeVisible() {
                return true;
            }
        };
        monthNavigationPanel.setOutputMarkupId(true);
        add(monthNavigationPanel);

        ReportDataProvider provider = new ReportDataProvider(this);
        provider.setFilter(filter.getObject());

        WorkDataTable table = new WorkDataTable(ID_TABLE, filter, true) {

            @Override
            protected void refresh(AjaxRequestTarget target) {
                target.add(PageWorkReport.this);
            }
        };
        table.setOutputMarkupId(true);
        add(table);

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
        setResponsePage(new PageWork(this.getPageReference()));
    }

    private void newBulkPerformed() {
        setResponsePage(new PageBulk(this.getPageReference()));
    }


    private void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setDashboardFilter(workFilter);
        target.add(PageWorkReport.this);
    }
}
