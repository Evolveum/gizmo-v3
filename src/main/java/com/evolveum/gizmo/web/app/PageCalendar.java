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
import com.evolveum.gizmo.component.calendar.*;
import com.evolveum.gizmo.component.form.CustomerProjectPartSearchPanel;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.data.Part;
import com.evolveum.gizmo.data.QAbstractTask;
import com.evolveum.gizmo.data.QWork;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MountPath("/app/calendar")
public class PageCalendar extends PageAppTemplate {

    private static final String ID_CALENDAR = "calendar";
    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_PROJECT = "project";
    private static final String ID_SHOW = "show";

    private static final String ID_BTN_PREVIOUS = "previous";
    private static final String ID_BTN_NEXT = "next";
    private static final String ID_MONTH = "month";

    private IModel<ReportFilterDto> model;

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


        Label month = new Label(ID_MONTH, new PropertyModel<>(model, ReportFilterDto.F_MONTH_YEAR));
        month.setOutputMarkupId(true);
        form.add(month);

        AjaxLink<String> prev = new AjaxLink<>(ID_BTN_PREVIOUS, createStringResource("fa-chevron")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                previousClicked(target);
            }
        };
        prev.setOutputMarkupId(true);
        form.add(prev);

        AjaxLink<String> next = new AjaxLink<>(ID_BTN_NEXT, createStringResource("fa-chevron")) {

            @Override
            public void onClick(AjaxRequestTarget target) {
                nextClicked(target);
            }
        };
        next.setOutputMarkupId(true);
        form.add(next);

        MultiselectDropDownInput<User> realizators = new
                MultiselectDropDownInput<>(ID_REALIZATOR,
                new PropertyModel<>(model, ReportFilterDto.F_REALIZATORS),
                GizmoUtils.createUsersModel(this),
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

        CalendarPanel calendarPanel = new CalendarPanel(ID_CALENDAR, createCalendarModel());
        add(calendarPanel);

    }

    private void refreshTable(AjaxRequestTarget target) {
        target.add(getFeedbackPanel());
        ReportFilterDto reportFilter = model.getObject();

        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setReportFilterDto(reportFilter);


        target.add(get(ID_CALENDAR));
    }

    private IModel<FullCalendar> createCalendarModel() {
        return () -> {

            List<Plugins> calendarPlugins = List.of(Plugins.DAY_GRID);
            HeaderToolbar headerToolbar = new HeaderToolbar();

            FullCalendar configNew =
                    new FullCalendar(
                            calendarPlugins,
                            headerToolbar,
                            Date.from(model.getObject().getDateFrom().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant()),
                            createEvents()
                    );

            return configNew;
        };
    }

    private List<Event> createEvents() {


        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);

        JPAQuery query = new JPAQuery(getEntityManager());
        query.from(QAbstractTask.abstractTask)
//                .join(work.realizator)
                .leftJoin(work.part.project);
        query.where(createPredicate());
        query.orderBy(work.date.asc());

        List<Tuple> vacations = query.select(work.workLength, work.realizator, work.date, work.part)
                .fetch();

        List<Event> events = new ArrayList<>();
        for (Tuple tuple : vacations) {
            User user = tuple.get(work.realizator);
            double workLength = tuple.get(work.workLength);
            double length = workLength / 8 * user.getAllocation();
            LocalDate date = tuple.get(work.date);
            Part part = tuple.get(work.part);
            String color = part.getColor() == null ? null : part.getColor();
            Date vacationDate = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Event event = new Event(user.getName(), user.getFullName() + " (" + length + "MD)", vacationDate, color).display("block");
            events.add(event);
        }

        return events;
    }

    private Predicate createPredicate() {
        ReportFilterDto filter = model.getObject();
        if (filter == null) {
            return null;
        }


        List<Predicate> list = ReportDataProvider.createPredicates(filter);
        if (list.isEmpty()) {
            return null;
        }

        BooleanBuilder bb = new BooleanBuilder();
        return bb.orAllOf(list.toArray(new Predicate[list.size()]));
    }

//    public static List<Predicate> createPredicates(ReportFilterDto filter) {
//        QAbstractTask task = QAbstractTask.abstractTask;
//        if (filter == null) {
//            return null;
//        }
//
//        List<Predicate> list = new ArrayList<>();
//        Predicate p = createListPredicate(filter.getRealizators(), task.realizator);
//        if (p != null) {
//            list.add(p);
//        }
//
//        if (!WorkType.ALL.equals(filter.getWorkType())) {
//            list.add(task.type.eq(filter.getWorkType().getType()));
//        }
//
//        ProjectSearchSettings settings = filter.getProjectSearchSettings();
//        p = createProjectListPredicate(settings.getCustomerProjectPartDtoList());
//        if (p != null) {
//            list.add(p);
//        }
//
//        if (filter.getDateFrom() != null) {
//            list.add(task.date.goe(filter.getDateFrom()));
//        }
//
//        if (filter.getDateTo() != null) {
//            list.add(task.date.loe(filter.getDateTo()));
//        }
//
//        return list;
//    }

//    private static Predicate createProjectListPredicate(List<CustomerProjectPartDto> list) {
//        if (list == null || list.isEmpty()) {
//            return null;
//        }
//
//        if (list.size() == 1) {
//            return createPredicate(list.get(0));
//        }
//
//        BooleanBuilder bb = new BooleanBuilder();
//        for (CustomerProjectPartDto dto : list) {
//            bb.or(createPredicate(dto));
//        }
//
//        return bb;
//    }

//    public static Predicate createPredicate(CustomerProjectPartDto dto) {
//        BooleanBuilder bb = new BooleanBuilder();
//
//        QAbstractTask task = QAbstractTask.abstractTask;
//        QLog log = task.as(QLog.class);
//        bb.or(log.customer.id.eq(dto.getCustomerId()));
//
//        QWork work = task.as(QWork.class);
//        if (dto.getPartId() != null) {
//            bb.or(work.part.id.eq(dto.getPartId()));
//        } else if (dto.getProjectId() != null) {
//            bb.or(work.part.project.id.eq(dto.getProjectId()));
//        } else if (dto.getCustomerId() != null) {
//            bb.or(work.part.project.customer.id.eq(dto.getCustomerId()));
//        }
//
//        return bb;
//    }

//    private static <T> Predicate createListPredicate(List<T> list, EntityPathBase<T> base) {
//        if (list == null || list.isEmpty()) {
//            return null;
//        }
//
//        if (list.size() == 1) {
//            return base.eq(list.get(0));
//        }
//
//        BooleanExpression expr = base.eq(list.get(0));
//        for (int i = 1; i < list.size(); i++) {
//            expr = expr.or(base.eq(list.get(i)));
//        }
//
//        return expr;
//    }

    private void previousClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = model.getObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.minusMonths(1));

        workFilter.setDateTo(workFilter.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendatNavigation(target, workFilter);
    }

    private void nextClicked(AjaxRequestTarget target) {
        ReportFilterDto workFilter = model.getObject();
        LocalDate defaultFrom = workFilter.getDateFrom();
        workFilter.setDateFrom(defaultFrom.plusMonths(1));

        workFilter.setDateTo(workFilter.getDateFrom().with(TemporalAdjusters.lastDayOfMonth()));
        handleCalendatNavigation(target, workFilter);
    }

    private void handleCalendatNavigation(AjaxRequestTarget target, ReportFilterDto workFilter) {
        GizmoAuthWebSession session = GizmoAuthWebSession.getSession();
        session.setReportFilterDto(workFilter);

        target.add(PageCalendar.this);
    }
}
