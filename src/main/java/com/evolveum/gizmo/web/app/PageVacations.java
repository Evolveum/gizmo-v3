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

import com.evolveum.gizmo.component.calendar.*;
import com.evolveum.gizmo.component.form.MultiselectDropDownInput;
import com.evolveum.gizmo.security.GizmoAuthWebSession;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.extensions.markup.html.form.datetime.LocalDateTextField;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.wicketstuff.annotation.mount.MountPath;
import com.evolveum.gizmo.component.AjaxSubmitButton;
import com.evolveum.gizmo.component.behavior.DateRangePickerBehavior;
import sk.lazyman.gizmo.component.calendar.*;
import com.evolveum.gizmo.data.QAbstractTask;
import com.evolveum.gizmo.data.QLog;
import com.evolveum.gizmo.data.QWork;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.WorkType;
import com.evolveum.gizmo.util.GizmoUtils;
import com.evolveum.gizmo.util.LoadableModel;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@MountPath("/app/vacations")
public class PageVacations extends PageAppTemplate {

    private static final String ID_CALENDAR = "calendar";
    private static final String ID_FORM = "form";
    private static final String ID_FROM = "from";
    private static final String ID_TO = "to";
    private static final String ID_REALIZATOR = "realizator";
    private static final String ID_PROJECT = "project";
    private static final String ID_SHOW = "show";

    private IModel<ReportFilterDto> model;

    public PageVacations() {

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

        LocalDateTextField from = new LocalDateTextField(ID_FROM, new PropertyModel<>(model, ReportFilterDto.F_DATE_FROM), "dd/MM/yyyy");
        from.setOutputMarkupId(true);
        from.add(new DateRangePickerBehavior());
        form.add(from);

        LocalDateTextField to = new LocalDateTextField(ID_TO, new PropertyModel<>(model, ReportFilterDto.F_DATE_TO), "dd/MM/yyyy");
        to.setOutputMarkupId(true);
        to.add(new DateRangePickerBehavior());
        form.add(to);


        MultiselectDropDownInput<User> realizators = new
                MultiselectDropDownInput<>(ID_REALIZATOR,
                new PropertyModel<>(model, ReportFilterDto.F_REALIZATORS),
                GizmoUtils.createUsersModel(this),
                GizmoUtils.createUserChoiceRenderer());
        realizators.setOutputMarkupId(true);
        form.add(realizators);

        MultiselectDropDownInput<CustomerProjectPartDto> projectCombo = new MultiselectDropDownInput<>(ID_PROJECT,
                new PropertyModel<>(model, ReportFilterDto.F_CUSTOM_PROJECT_PART),
                GizmoUtils.createCustomerProjectPartList(this, true, true, true),
                GizmoUtils.createCustomerProjectPartRenderer());
        form.add(projectCombo);

        AjaxSubmitButton preview = new AjaxSubmitButton(ID_SHOW, createStringResource("PageVacations.button.show")) {

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
                .join(work.realizator)
                .leftJoin(work.part);
        query.where(createPredicate());
        query.orderBy(task.date.asc());

        List<Tuple> vacations = query.select(QAbstractTask.abstractTask.workLength, QAbstractTask.abstractTask.realizator.name, QAbstractTask.abstractTask.date)
                .fetch();

        List<Event> events = new ArrayList<>();
        for (Tuple tuple : vacations) {
            String username = tuple.get(QAbstractTask.abstractTask.realizator.name);
            double length = tuple.get(QAbstractTask.abstractTask.workLength);
            LocalDate date = tuple.get(QAbstractTask.abstractTask.date);
            Date vacationDate = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Event event = new Event(username, username + " (" + length + ")", vacationDate, null).display("block");
            events.add(event);
        }

        return events;
    }

    private Predicate createPredicate() {
        ReportFilterDto filter = model.getObject();
        if (filter == null) {
            return null;
        }

        List<Predicate> list = createPredicates(filter);
        if (list.isEmpty()) {
            return null;
        }

        BooleanBuilder bb = new BooleanBuilder();
        return bb.orAllOf(list.toArray(new Predicate[list.size()]));
    }

    public static List<Predicate> createPredicates(ReportFilterDto filter) {
        QAbstractTask task = QAbstractTask.abstractTask;
        if (filter == null) {
            return null;
        }

        List<Predicate> list = new ArrayList<>();
        Predicate p = createListPredicate(filter.getRealizators(), task.realizator);
        if (p != null) {
            list.add(p);
        }

        if (!WorkType.ALL.equals(filter.getWorkType())) {
            list.add(task.type.eq(filter.getWorkType().getType()));
        }

        p = createProjectListPredicate(filter.getCustomerProjectPartDtos());
        if (p != null) {
            list.add(p);
        }

        if (filter.getDateFrom() != null) {
            list.add(task.date.goe(filter.getDateFrom()));
        }

        if (filter.getDateTo() != null) {
            list.add(task.date.loe(filter.getDateTo()));
        }

        return list;
    }

    private static Predicate createProjectListPredicate(List<CustomerProjectPartDto> list) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        if (list.size() == 1) {
            return createPredicate(list.get(0));
        }

        BooleanBuilder bb = new BooleanBuilder();
        for (CustomerProjectPartDto dto : list) {
            bb.or(createPredicate(dto));
        }

        return bb;
    }

    public static Predicate createPredicate(CustomerProjectPartDto dto) {
        BooleanBuilder bb = new BooleanBuilder();

        QAbstractTask task = QAbstractTask.abstractTask;
        QLog log = task.as(QLog.class);
        bb.or(log.customer.id.eq(dto.getCustomerId()));

        QWork work = task.as(QWork.class);
        if (dto.getPartId() != null) {
            bb.or(work.part.id.eq(dto.getPartId()));
        } else if (dto.getProjectId() != null) {
            bb.or(work.part.project.id.eq(dto.getProjectId()));
        } else if (dto.getCustomerId() != null) {
            bb.or(work.part.project.customer.id.eq(dto.getCustomerId()));
        }

        return bb;
    }

    private static <T> Predicate createListPredicate(List<T> list, EntityPathBase<T> base) {
        if (list == null || list.isEmpty()) {
            return null;
        }

        if (list.size() == 1) {
            return base.eq(list.get(0));
        }

        BooleanExpression expr = base.eq(list.get(0));
        for (int i = 1; i < list.size(); i++) {
            expr = expr.or(base.eq(list.get(i)));
        }

        return expr;
    }
}
