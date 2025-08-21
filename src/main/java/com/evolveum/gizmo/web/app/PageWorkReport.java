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


import com.evolveum.gizmo.component.data.*;
import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.data.provider.SummaryUserDataProvider;
import com.evolveum.gizmo.dto.ProgressDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.UserSummary;
import com.evolveum.gizmo.dto.WorkDto;
import com.evolveum.gizmo.repository.AbstractTaskRepository;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
@MountPath("/app/workReport")
public class PageWorkReport extends PageAppTemplate {

    private static final String ID_BTN_PREVIOUS = "previous";
    private static final String ID_BTN_NEXT = "next";
    private static final String ID_MONTH = "month";
    private static final String ID_SUMMARY_PARTS = "summaryParts";
    private static final String ID_TABLE = "table";

    private static final String ID_BTN_NEW_WORK = "newWork";
    private static final String ID_BTN_NEW_BULK = "newBulk";

    private static final String ID_CALENDAR = "calendar";
    private static final String ID_PROGRESS_BAR = "progressBar";

    private IModel<ReportFilterDto> filter;

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

    private IModel<ReportFilterDto> getFilterModel() {
        return filter;
    }

    private void initLayout() {

        Label month = new Label(ID_MONTH, new PropertyModel<>(filter, ReportFilterDto.F_MONTH_YEAR));
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
        progress.setOutputMarkupId(true);
        add(progress);

        ReportDataProvider provider = new ReportDataProvider(this);
        provider.setFilter(filter.getObject());

        List<IColumn<WorkDto, String>> columns = createColumns();
        TablePanel<WorkDto> table = new TablePanel<>(ID_TABLE, provider, columns, 20);
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

    private IModel<ProgressDto> loadProgressModel() {
        return new LoadableModel<>(true) {

            @Override
            protected ProgressDto load() {
                LocalDate firstDay = filter.getObject().getDateFrom();
                LocalDate lastDay = filter.getObject().getDateTo();
                long totalDates = firstDay.datesUntil(lastDay)
                        .filter(date -> isNotWeekend(date))
                        .filter(date -> GizmoUtils.isNotHoliday(date))
                        .count();

                SummaryUserDataProvider summaryPerUser = new SummaryUserDataProvider(PageWorkReport.this);
                ReportFilterDto filter = new ReportFilterDto();
                ReportFilterDto originalFilter = getFilterModel().getObject();
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

    private boolean isNotWeekend(LocalDate date) {
        DayOfWeek dayOfWeek = date.getDayOfWeek();
        return dayOfWeek != DayOfWeek.SATURDAY
                && dayOfWeek != DayOfWeek.SUNDAY;
    }


    private void newWorkPerformed() {
        setResponsePage(PageWork.class);
    }

    private void newBulkPerformed() {
        setResponsePage(PageBulk.class);
    }

    private void previousClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = filter.getObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.minusMonths(1));

        workFilter.setDateTo(workFilter.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendarNavigation(target, workFilter);
    }

    private void nextClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = filter.getObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.plusMonths(1));


        workFilter.setDateTo(workFilter.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendarNavigation(target, workFilter);
    }

    private void handleCalendarNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setDashboardFilter(workFilter);
        target.add(PageWorkReport.this);
    }


    //date, length (invoice), realizator, project, description (WORK)
    //date, length (0.0), realizator, customer, description, attachments(icon) (LOG)
    private List<IColumn<WorkDto, String>> createColumns() {
        List<IColumn<WorkDto, String>> columns = new ArrayList<>();

        columns.add(new EditablePropertyColumn<>(createStringResource("AbstractTask.date"), AbstractTask.F_DATE));
        columns.add(GizmoUtils.createWorkInvoiceColumn(this));
        columns.add(GizmoUtils.createWorkTimeRangeColumn(this));
        columns.add(GizmoUtils.createWorkProjectColumn(this));
        columns.add(new EditablePropertyColumn<>(createStringResource("AbstractTask.trackId"), AbstractTask.F_TRACK_ID));
        columns.add(new EditablePropertyColumn<>(createStringResource("AbstractTask.description"), AbstractTask.F_DESCRIPTION));
        columns.add(new LinkIconColumn<>(new Model<>("")) {

            @Override
            protected IModel<String> createIconModel(IModel<WorkDto> rowModel) {
                return new Model<>("fa fa-trash text-danger");
            }

            @Override
            protected IModel<String> createTitleModel(IModel<WorkDto> rowModel) {
                return PageWorkReport.this.createStringResource("PageDashboard.delete");
            }

            @Override
            protected void onClickPerformed(AjaxRequestTarget target, IModel<WorkDto> rowModel, AjaxLink link) {
                deletePerformed(target, rowModel.getObject());
            }
        });

        columns.add(new LinkIconColumn<>(new Model<>("")) {

            @Override
            protected IModel<String> createIconModel(IModel<WorkDto> rowModel) {
                return new Model<>("fa fa-edit text-default");
            }

            @Override
            protected IModel<String> createTitleModel(IModel<WorkDto> rowModel) {
                return PageWorkReport.this.createStringResource("PageDashboard.edit");
            }

            @Override
            protected void onClickPerformed(AjaxRequestTarget target, IModel<WorkDto> rowModel, AjaxLink link) {
                editWorkReportPerformed(target, rowModel.getObject(), link);

            }
        });

        return columns;
    }

    private void editWorkReportPerformed(AjaxRequestTarget target, WorkDto work, AjaxLink link) {
        work.setEditable(true);
        work.setDescription("added description");
        target.add(link.findParent(MyRowItem.class));
    }

    private void deletePerformed(AjaxRequestTarget target, WorkDto task) {
        //todo add confirmation
        try {
            AbstractTaskRepository repository = getAbstractTaskRepository();
            repository.deleteById(task.getId());

            success(createStringResource("Message.successfullyDeleted").getString());
            target.add(getFeedbackPanel(), get(ID_PROGRESS_BAR), get(ID_TABLE), get(ID_CALENDAR), get(ID_SUMMARY_PARTS));
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }
}
