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

import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.calendar.CalendarEventsProvider;
import com.evolveum.gizmo.component.calendar.CalendarPanel;
import com.evolveum.gizmo.component.data.MonthNavigationPanel;
import com.evolveum.gizmo.component.form.CustomerProjectPartSearchPanel;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

@MountPath("/app/calendar")
public class PageCalendar extends PageAppTemplate {

    private static final String ID_CALENDAR = "calendar";
    private static final String ID_FORM = "form";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_PROJECT = "project";
    private static final String ID_SHOW = "show";

    private static final String ID_MONTH_NAVIGATION = "monthNavigation";

    private final IModel<ReportFilterDto> model;

    public PageCalendar() {

        model = new LoadableModel<>(false) {

            @Override
            protected ReportFilterDto load() {
                GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
                ReportFilterDto filter = session.getReportFilterDto();
                if (filter == null) {
                    filter = new ReportFilterDto();
                    filter.setDateFrom(GizmoUtils.createWorkDefaultFrom());
                    filter.setDateTo(GizmoUtils.createWorkDefaultTo());
                }
                return filter;
            }
        };

        initLayout();
    }

    private void initLayout() {

        Form<ReportFilterDto> form = new Form<>(ID_FORM);
        form.setOutputMarkupId(true);
        add(form);

        MonthNavigationPanel monthNavigation = new MonthNavigationPanel(ID_MONTH_NAVIGATION, model) {

            @Override
            protected void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
                PageCalendar.this.handleCalendarNavigation(target, workFilter);
            }

        };
        monthNavigation.setOutputMarkupId(true);
        form.add(monthNavigation);

        MultiselectDropDownInput<User> realizators = new
                MultiselectDropDownInput<>(ID_REALIZATOR,
                new PropertyModel<>(model, ReportFilterDto.F_REALIZATORS),
                GizmoUtils.createUsersModel(this, model),
                GizmoUtils.createUserChoiceRenderer());
        realizators.setOutputMarkupId(true);
        form.add(realizators);

        CustomerProjectPartSearchPanel customerProjectSearchPanel = new CustomerProjectPartSearchPanel(ID_PROJECT, new PropertyModel<>(model, ReportFilterDto.F_PROJECT_SEARCH_SETTINGS));
        customerProjectSearchPanel.setOutputMarkupId(true);
        form.add(customerProjectSearchPanel);

        AjaxSubmitButton preview = new AjaxSubmitButton(ID_SHOW, createStringResource("PageCalendar.button.show")) {

            @Override
            protected void onSubmit(AjaxRequestTarget target) {
                refreshTable(target);
            }

            @Override
            protected void onError(AjaxRequestTarget target) {
                target.add(getFeedbackPanel());
                super.onError(target);
            }
        };
        form.add(preview);

        CalendarEventsProvider eventsProvider = new CalendarEventsProvider(PageCalendar.this, model, false);
        CalendarPanel calendarPanel = new CalendarPanel(ID_CALENDAR, eventsProvider);
        add(calendarPanel);

    }

    private void refreshTable(AjaxRequestTarget target) {
        target.add(getFeedbackPanel());
        ReportFilterDto reportFilter = model.getObject();

        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setReportFilterDto(reportFilter);


        target.add(get(ID_CALENDAR));
    }

    private void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setReportFilterDto(workFilter);

        target.add(PageCalendar.this);
    }
}
