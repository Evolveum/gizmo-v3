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
import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import com.evolveum.gizmo.component.form.EmptyOnChangeAjaxBehavior;
import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ProgressDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.UserSummary;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.ajax.markup.html.form.AjaxCheckBox;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class MonthNavigationPanel extends SimplePanel<ReportFilterDto> {

    private static final String ID_MONTH_NAVIGATION = "monthNavigation";
    private static final String ID_BTN_PREVIOUS = "previous";
    private static final String ID_BTN_NEXT = "next";
    private static final String ID_MONTH = "month";

    private static final String ID_USE_CUSTOM_DATE_RANGE = "useCustomDateRange";

    private static final String ID_DATE_RANGE_NAVIGATION = "dateRangeNavigation";
    protected static final String ID_FROM = "from";
    protected static final String ID_TO = "to";

    private static final String ID_PROGRESS_BAR = "progressBar";

    private boolean customDateRange;

    protected MonthNavigationPanel(String id, IModel<ReportFilterDto> model) {
        super(id, model);
    }

    @Override
    protected void initLayout() {
        WebMarkupContainer monthNavigation = new WebMarkupContainer(ID_MONTH_NAVIGATION);
        add(monthNavigation);
        monthNavigation.add(new VisibleEnableBehaviour() {
            @Override
            public boolean isVisible() {
                return !customDateRange;
            }
        });

        Label month = new Label(ID_MONTH, new PropertyModel<>(getModel(), ReportFilterDto.F_MONTH_YEAR));
        month.setOutputMarkupId(true);
        monthNavigation.add(month);

        AjaxLink<String> prev = new AjaxLink<>(ID_BTN_PREVIOUS, createStringResource("fa-chevron")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                previousClicked(target);
            }
        };
        prev.setOutputMarkupId(true);
        monthNavigation.add(prev);

        AjaxLink<String> next = new AjaxLink<>(ID_BTN_NEXT, createStringResource("fa-chevron")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                nextClicked(target);
            }
        };
        next.setOutputMarkupId(true);
        monthNavigation.add(next);

        ProgressPanel progress = new ProgressPanel(ID_PROGRESS_BAR, loadProgressModel());
        progress.add(new VisibleEnableBehaviour() {

            @Override
            public boolean isVisible() {
                return isProgressPanelVisible();
            }
        });
        progress.setOutputMarkupId(true);
        add(progress);

        WebMarkupContainer dateRangeContainer = new WebMarkupContainer(ID_DATE_RANGE_NAVIGATION);
        add(dateRangeContainer);
        dateRangeContainer.add(new VisibleEnableBehaviour() {
            @Override
            public boolean isVisible() {
                return customDateRange;
            }
        });

        AjaxCheckBox useCustomDateRange = new AjaxCheckBox(ID_USE_CUSTOM_DATE_RANGE,
                new PropertyModel<>(this, "customDateRange")) {

            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                ReportFilterDto filter = MonthNavigationPanel.this.getModelObject();
                if (!customDateRange) {
                    filter.setDateTo(GizmoUtils.computeWorkFrom(filter.getDateFrom()));
                    filter.setDateTo(GizmoUtils.computeWorkTo(filter.getDateTo()));
                    handleCalendarNavigation(target, filter);
                }
                target.add(MonthNavigationPanel.this);
            }
        };
        useCustomDateRange.setOutputMarkupId(true);
        useCustomDateRange.add(new VisibleEnableBehaviour() {
            @Override
            public boolean isVisible() {
                return isCustomDateRangeVisible();
            }
        });
        add(useCustomDateRange);

        LocalDateTextField from = new LocalDateTextField(ID_FROM,
                new PropertyModel<>(getModel(), ReportFilterDto.F_DATE_FROM), "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new EmptyOnChangeAjaxBehavior());
        from.add(new DateRangePickerBehavior() {
            @Override protected void onEvent(AjaxRequestTarget target) { setDateTo(target); }
        });
        dateRangeContainer.add(from);

        LocalDateTextField to = new LocalDateTextField(ID_TO,
                new PropertyModel<>(getModel(), ReportFilterDto.F_DATE_TO), "dd/MM/yyyy");
        to.setOutputMarkupId(true);
        to.add(new EmptyOnChangeAjaxBehavior());
        to.add(new DateRangePickerBehavior() {
            @Override
            protected void onEvent(AjaxRequestTarget target) {
                handleCalendarNavigation(target, getModelObject());
            }
        });
        dateRangeContainer.add(to);
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
                        .filter(GizmoUtils::isNotHoliday)
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

    protected void setDateTo(AjaxRequestTarget target) {
        ReportFilterDto f = getModel().getObject();
        f.setDateTo(f.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        LocalDateTextField dateTo = (LocalDateTextField) get(createComponentPath(ID_DATE_RANGE_NAVIGATION, ID_TO));
        target.add(dateTo);
        handleCalendarNavigation(target, f);
    }


    protected boolean isProgressPanelVisible() {
        return false;
    }

    /**
     * Override if the custom from-to date fields should be visible
     * @return
     */
    protected boolean isCustomDateRangeVisible() {
        return false;
    }

}
