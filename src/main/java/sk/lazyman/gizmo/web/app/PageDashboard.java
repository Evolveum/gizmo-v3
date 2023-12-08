/*
 * Copyright 2015 Viliam Repan (lazyman)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.lazyman.gizmo.web.app;


import net.ftlines.wicket.fullcalendar.*;
import net.ftlines.wicket.fullcalendar.callback.ClickedEvent;
import net.ftlines.wicket.fullcalendar.callback.SelectedRange;
import net.ftlines.wicket.fullcalendar.callback.View;
import org.apache.commons.lang3.StringUtils;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.extensions.markup.html.repeater.data.table.IColumn;
import org.apache.wicket.extensions.markup.html.repeater.data.table.PropertyColumn;
import org.apache.wicket.markup.head.IHeaderResponse;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.apache.wicket.request.mapper.parameter.PageParameters;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.wicketstuff.annotation.mount.MountPath;
import sk.lazyman.gizmo.component.SummaryChartPanel;
import sk.lazyman.gizmo.component.SummaryPanel;
import sk.lazyman.gizmo.component.calendar.CalendarPanel;
import sk.lazyman.gizmo.component.calendar.Event;
import sk.lazyman.gizmo.component.calendar.HeaderToolbar;
import sk.lazyman.gizmo.component.calendar.Plugins;
import sk.lazyman.gizmo.component.data.LinkColumn;
import sk.lazyman.gizmo.component.data.LinkIconColumn;
import sk.lazyman.gizmo.component.data.TablePanel;
import sk.lazyman.gizmo.data.AbstractTask;
import sk.lazyman.gizmo.data.Log;
import sk.lazyman.gizmo.data.Work;
import sk.lazyman.gizmo.data.provider.AbstractTaskDataProvider;
import sk.lazyman.gizmo.data.provider.SummaryDataProvider;
import sk.lazyman.gizmo.data.provider.SummaryPartsDataProvider;
import sk.lazyman.gizmo.dto.ReportFilterDto;
import sk.lazyman.gizmo.dto.SummaryPanelDto;
import sk.lazyman.gizmo.dto.TaskLength;
import sk.lazyman.gizmo.repository.AbstractTaskRepository;
import sk.lazyman.gizmo.security.GizmoAuthWebSession;
import sk.lazyman.gizmo.security.GizmoPrincipal;
import sk.lazyman.gizmo.security.SecurityUtils;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.util.LoadableModel;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
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

    private IModel<sk.lazyman.gizmo.component.calendar.FullCalendar> createCalendarModel() {
        return () -> {

            List<Plugins> calendarPlugins = List.of(Plugins.DAY_GRID);
            HeaderToolbar headerToolbar = new HeaderToolbar();

            sk.lazyman.gizmo.component.calendar.FullCalendar configNew =
                    new sk.lazyman.gizmo.component.calendar.FullCalendar(
                            calendarPlugins,
                            headerToolbar,
                            Date.from(filter.getObject().getDateFrom().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            createEvents()
                    );

            return configNew;
        };
    }

    private List<Event> createEvents() {
        IModel<SummaryPanelDto> summaryModel = createSummaryModel();
        SummaryPanelDto summary = summaryModel.getObject();
        Map<LocalDate, TaskLength> workSummaryPerDeay = summary.getDates();

        List<Event> events = new ArrayList<>();
        int i = 0;
        for (Map.Entry<LocalDate, TaskLength> entry : workSummaryPerDeay.entrySet()) {
            i++;

            TaskLength length = entry.getValue();
            String lenghtAsString = StringUtils.join(new Object[]{length.getLength(), length.getInvoice()}, '/');
            LocalDate startDay = entry.getKey();
            Date day = Date.from(startDay.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            boolean isWeekend = DayOfWeek.SATURDAY == startDay.getDayOfWeek() || DayOfWeek.SUNDAY == startDay.getDayOfWeek();
            boolean isFullDay = length.getLength() >= 8;
            Event event = new Event(Integer.toString(i), lenghtAsString, day, isFullDay ? "green" : "red");
            events.add(event);
        }
        return events;
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
