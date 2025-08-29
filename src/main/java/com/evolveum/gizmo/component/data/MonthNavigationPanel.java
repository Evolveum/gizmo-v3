/*
 *  Copyright (C) 2025 Evolveum
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

package com.evolveum.gizmo.component.data;

import com.evolveum.gizmo.component.SimplePanel;
import com.evolveum.gizmo.component.VisibleEnableBehaviour;
import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ProgressDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.UserSummary;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class MonthNavigationPanel extends SimplePanel<ReportFilterDto> {

    private static final String ID_BTN_PREVIOUS = "previous";
    private static final String ID_BTN_NEXT = "next";
    private static final String ID_MONTH = "month";

    private static final String ID_PROGRESS_BAR = "progressBar";

    protected MonthNavigationPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        Label month = new Label(ID_MONTH, new PropertyModel<>(getModel(), ReportFilterDto.F_MONTH_YEAR));
        month.setOutputMarkupId(true);
        add(month);

        AjaxLink<String> prev = new AjaxLink<>(ID_BTN_PREVIOUS, createStringResource("fa-chevron")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                previousClicked(target);
            }
        };
        prev.setOutputMarkupId(true);
        add(prev);

        AjaxLink<String> next = new AjaxLink<>(ID_BTN_NEXT, createStringResource("fa-chevron")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                nextClicked(target);
            }
        };
        next.setOutputMarkupId(true);
        add(next);

        ProgressPanel progress = new ProgressPanel(ID_PROGRESS_BAR, loadProgressModel());
        progress.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return isProgressPanelVisible();
            }
        });
        progress.setOutputMarkupId(true);
        add(progress);

    }

    private void previousClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = getModelObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.minusMonths(1));

        workFilter.setDateTo(workFilter.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendarNavigation(target, workFilter);
    }

    private void nextClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = getModelObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.plusMonths(1));


        workFilter.setDateTo(workFilter.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendarNavigation(target, workFilter);
    }

    private IModel<ProgressDto> loadProgressModel() {
        return new LoadableModel<>(true) {

            @Override
            protected ProgressDto load() {
                ReportFilterDto filter = getModelObject();
                LocalDate firstDay = filter.getDateFrom();
                LocalDate lastDay = filter.getDateTo();
                long totalDates = firstDay.datesUntil(lastDay)
                        .filter(date -> isNotWeekend(date))
                        .filter(date -> GizmoUtils.isNotHoliday(date))
                        .count();

                SummaryUserDataProvider summaryPerUser = new SummaryUserDataProvider(getPageTemplate());
                ReportFilterDto originalFilter = getModelObject();
                filter.setRealizators(originalFilter.getRealizators());
                filter.setDateFrom(originalFilter.getDateFrom());
                filter.setDateTo(originalFilter.getDateTo());
                List<UserSummary> userSummary = summaryPerUser.createSummary(filter);
                if (userSummary.isEmpty()) {
                    return new ProgressDto(totalDates, 0);
                }
                double currentLength = userSummary.get(0).getLength() / (8 * filter.getRealizators().get(0).getAllocation());
                return new ProgressDto(totalDates, currentLength);
            }
        };
    }

    protected void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
        throw new UnsupportedOperationException("Implement in the caller class");
    }

    private boolean isNotWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY
                && dayOfWeek != DayOfWeek.SUNDAY;
    }

    protected boolean isProgressPanelVisible() {
        return false;
    }

}
