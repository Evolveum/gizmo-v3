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


import com.evolveum.gizmo.component.AjaxButton;
import com.evolveum.gizmo.component.SummaryChartPanel;
import com.evolveum.gizmo.component.SummaryPanel;
import com.evolveum.gizmo.component.calendar.CalendarPanel;
import com.evolveum.gizmo.component.calendar.Event;
import com.evolveum.gizmo.component.calendar.HeaderToolbar;
import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.provider.AbstractTaskDataProvider;
import com.evolveum.gizmo.data.provider.SummaryDataProvider;
import com.evolveum.gizmo.data.provider.SummaryPartsDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.SummaryPanelDto;
import com.evolveum.gizmo.dto.TaskLength;
import com.evolveum.gizmo.repository.AbstractTaskRepository;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.security.GizmoPrincipal;
import com.evolveum.gizmo.security.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.component.calendar.Plugins;
import com.evolveum.gizmo.component.data.LinkColumn;
import com.evolveum.gizmo.component.data.LinkIconColumn;
import com.evolveum.gizmo.component.data.TablePanel;
import com.evolveum.gizmo.data.Log;
import com.evolveum.gizmo.data.Work;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * @author lazyman
 */
@MountPath(value = "/dashboard", alt = "/app")
public class PageDashboard extends PageAppTemplate {

    private static final String ID_BTN_PREVIOUS = "previous";
    private static final String ID_BTN_NEXT = "next";
    private static final String ID_MONTH = "month";
    private static final String ID_SUMMARY = "summary";
    private static final String ID_SUMMARY_PARTS = "summaryParts";
    private static final String ID_TABLE = "table";

    private static final String ID_BTN_NEW_WORK = "newWork";
    private static final String ID_BTN_NEW_BULK = "newBulk";

    private static final String ID_CALENDAR = "calendar";

    private IModel<ReportFilterDto> filter;

    public PageDashboard() {
        filter = new LoadableModel<>(false) {

            @Override
            protected ReportFilterDto load() {
                GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
                ReportFilterDto dto = session.getDashboardFilter();
                if (dto == null) {
                    dto = new ReportFilterDto();
                    dto.setDateFrom(GizmoUtils.createWorkDefaultFrom());
                    dto.setDateTo(GizmoUtils.createWorkDefaultTo());

                    GizmoPrincipal principal = SecurityUtils.getPrincipalUser();
                    dto.getRealizators().add(principal.getUser());

                    session.setDashboardFilter(dto);
                }

                return dto;
            }
        };

        initLayout();
    }

    private IModel<ReportFilterDto> getFilterModel() {
        return filter;
    }

    private void initLayout() {

        Label month = new Label(ID_MONTH, new PropertyModel<>(filter, "month"));
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

        AjaxLink<String> newWork = new AjaxLink<>(ID_BTN_NEW_WORK, createStringResource("PageDashboard.newWork")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newWorkPerformed();
            }
        };
        newWork.setOutputMarkupId(true);
        add(newWork);

        AjaxLink<String> newBulk = new AjaxLink<>(ID_BTN_NEW_BULK, createStringResource("PageDashboard.newBulk")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                newBulkPerformed();
            }
        };
        newBulk.setOutputMarkupId(true);
        add(newBulk);

        AbstractTaskDataProvider provider = new AbstractTaskDataProvider(this);
        provider.setFilter(filter.getObject());

        List<IColumn<AbstractTask, String>> columns = createColumns();
        TablePanel<AbstractTask> table = new TablePanel<>(ID_TABLE, provider, columns, 50);
        table.setOutputMarkupId(true);
        add(table);

        SummaryPartsDataProvider partsProvider = new SummaryPartsDataProvider(this);
        SummaryChartPanel chart = new SummaryChartPanel(ID_SUMMARY_PARTS, partsProvider, getFilterModel());
        chart.setOutputMarkupId(true);
        add(chart);


        CalendarPanel calendarPanel = new CalendarPanel(ID_CALENDAR, createCalendarModel());
        add(calendarPanel);

    }

    private IModel<com.evolveum.gizmo.component.calendar.FullCalendar> createCalendarModel() {
        return () -> {

            List<Plugins> calendarPlugins = List.of(Plugins.DAY_GRID);
            HeaderToolbar headerToolbar = new HeaderToolbar();

            com.evolveum.gizmo.component.calendar.FullCalendar configNew =
                    new com.evolveum.gizmo.component.calendar.FullCalendar(
                            calendarPlugins,
                            headerToolbar,
                            Date.from(filter.getObject().getDateFrom().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            createEvents()
                    );

            return configNew;
        };
    }

    private List<com.evolveum.gizmo.component.calendar.Event> createEvents() {
        IModel<SummaryPanelDto> summaryModel = createSummaryModel();
        SummaryPanelDto summary = summaryModel.getObject();
        Map<LocalDate, TaskLength> workSummaryPerDeay = summary.getDates();

        List<com.evolveum.gizmo.component.calendar.Event> events = new ArrayList<>();
        int i = 0;
        for (Map.Entry<LocalDate, TaskLength> entry : workSummaryPerDeay.entrySet()) {
            i++;

            TaskLength length = entry.getValue();
            String lenghtAsString = StringUtils.join(new Object[]{length.getLength(), length.getInvoice()}, '/');
            LocalDate startDay = entry.getKey();
            Date day = Date.from(startDay.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            boolean isWeekend = DayOfWeek.SATURDAY == startDay.getDayOfWeek() || DayOfWeek.SUNDAY == startDay.getDayOfWeek();
            boolean isFullDay = length.getLength() >= 8;
            com.evolveum.gizmo.component.calendar.Event event = new Event(Integer.toString(i), lenghtAsString, day, isFullDay ? "green" : "red");
            events.add(event);
        }
        return events;
    }

    private void newWorkPerformed() {
        setResponsePage(PageWork.class);
    }

    private void newBulkPerformed() {
        setResponsePage(PageBulk.class);
    }

    private void refreshComponents(AjaxRequestTarget target) {
        target.add(get(ID_TABLE));
        target.add(get(ID_CALENDAR));
        target.add(get(ID_SUMMARY_PARTS));
    }

    private IModel<SummaryPanelDto> createSummaryModel() {
        return new LoadableModel<>(false) {

            @Override
            protected SummaryPanelDto load() {
                SummaryDataProvider provider = new SummaryDataProvider(PageDashboard.this);
                return provider.createSummary(getFilterModel().getObject());
            }
        };
    }

    private void previousClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = filter.getObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.minusMonths(1));
        LocalDate defaultTo = workFilter.getDateTo();
        workFilter.setDateTo(defaultTo.minusMonths(1));
        handleCalendatNavigation(target, workFilter);
    }

    private void nextClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = filter.getObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.plusMonths(1));

        LocalDate defaultTo = workFilter.getDateTo();
        workFilter.setDateTo(defaultTo.plusMonths(1));
        handleCalendatNavigation(target, workFilter);
    }

    private void handleCalendatNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setDashboardFilter(workFilter);
        SummaryPanel summaryPanel = (SummaryPanel) get(ID_SUMMARY);
        if (summaryPanel != null) {
            ((LoadableModel) summaryPanel.getModel()).reset();
        }
        target.add(PageDashboard.this);
    }


    //date, length (invoice), realizator, project, description (WORK)
    //date, length (0.0), realizator, customer, description, attachments(icon) (LOG)
    private List<IColumn<AbstractTask, String>> createColumns() {
        List<IColumn<AbstractTask, String>> columns = new ArrayList<>();

        columns.add(new LinkColumn<>(createStringResource("AbstractTask.date"), AbstractTask.F_DATE) {

            @Override
            protected IModel<String> createLinkModel(final IModel<AbstractTask> rowModel) {
                return () -> {
                    PropertyModel<LocalDate> propertyModel = new PropertyModel<>(rowModel, getPropertyExpression());
                    LocalDate date = propertyModel.getObject();
                    return date.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
                };
            }

            @Override
            public void onClick(AjaxRequestTarget target, IModel<AbstractTask> rowModel) {
                AbstractTask task = rowModel.getObject();
                switch (task.getType()) {
                    case LOG:
                        logDetailsPerformed((Log) task);
                        break;
                    case WORK:
                        workDetailsPerformed((Work) task);
                        break;
                }
            }
        });
        columns.add(GizmoUtils.createWorkInvoiceColumn(this));
        columns.add(GizmoUtils.createWorkProjectColumn(this));
        columns.add(new PropertyColumn<>(createStringResource("AbstractTask.trackId"), AbstractTask.F_TRACK_ID));
        columns.add(new PropertyColumn<>(createStringResource("AbstractTask.description"), AbstractTask.F_DESCRIPTION));
        columns.add(new LinkIconColumn<>(new Model<>("")) {

            @Override
            protected IModel<String> createIconModel(IModel<AbstractTask> rowModel) {
                return new Model<>("fa fa-lg fa-trash-o text-danger");
            }

            @Override
            protected IModel<String> createTitleModel(IModel<AbstractTask> rowModel) {
                return PageDashboard.this.createStringResource("PageDashboard.delete");
            }

            @Override
            protected void onClickPerformed(AjaxRequestTarget target, IModel<AbstractTask> rowModel, AjaxLink link) {
                deletePerformed(target, rowModel.getObject());
            }
        });

        return columns;
    }

    private void workDetailsPerformed(Work work) {
        PageParameters params = new PageParameters();
        params.add(PageWork.WORK_ID, work.getId());

        setResponsePage(PageWork.class, params);
    }

    private void logDetailsPerformed(Log log) {
        PageParameters params = new PageParameters();
        params.add(PageLog.LOG_ID, log.getId());

        setResponsePage(PageLog.class, params);
    }

    private void deletePerformed(AjaxRequestTarget target, AbstractTask task) {
        //todo add confirmation
        try {
            AbstractTaskRepository repository = getAbstractTaskRepository();
            repository.deleteById(task.getId());

            success(createStringResource("Message.successfullyDeleted").getString());
            target.add(getFeedbackPanel(), get(ID_TABLE), get(ID_SUMMARY), get(ID_SUMMARY_PARTS));
        } catch (Exception ex) {
            handleGuiException(this, "Message.couldntSaveWork", ex, target);
        }
    }
}
