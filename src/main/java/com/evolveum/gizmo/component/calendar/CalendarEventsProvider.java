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

package com.evolveum.gizmo.component.calendar;

import com.evolveum.gizmo.data.QAbstractTask;
import com.evolveum.gizmo.data.QWork;
import com.evolveum.gizmo.data.User;
import com.evolveum.gizmo.data.provider.ReportDataProvider;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.web.app.PageAppTemplate;
import com.querydsl.core.Tuple;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.wicket.model.IModel;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CalendarEventsProvider implements Serializable {

    private PageAppTemplate page;
    private IModel<ReportFilterDto> filterModel;
    private boolean showHours;

    public CalendarEventsProvider(PageAppTemplate page, IModel<ReportFilterDto> filterModel) {
        this(page, filterModel, true);
    }

    public CalendarEventsProvider(PageAppTemplate page, IModel<ReportFilterDto> filterModel, boolean showHours) {
        this.page = page;
        this.filterModel = filterModel;
        this.showHours = showHours;
    }

    public Date getStartDateForEvents() {
        return Date.from(filterModel.getObject().getDateFrom().atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
    }

    public List<Event> createEvents() {
        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);
        ReportFilterDto filter = filterModel.getObject();

        JPAQuery<?> query = ReportDataProvider.query(task, page.getEntityManager(), filter);
        query.groupBy(work.date, work.realizator);

        List<Tuple> vacations = query.select(work.date, work.workLength.sum(), work.realizator)
                .fetch();

        boolean oneUserPreview = filter.getRealizators().size() == 1;

        List<Event> events = new ArrayList<>();
        for (Tuple tuple : vacations) {
            User user = tuple.get(work.realizator);
            Double workLength = tuple.get(1, Double.class);
            if (!showHours) {
                workLength = workLength / 8 * user.getAllocation();
            }

            LocalDate date = tuple.get(0, LocalDate.class);

            boolean isFullDay = workLength >= (8 * user.getAllocation());
            String color = isFullDay ? "green" : "red";
            Date vacationDate = Date.from(date.atStartOfDay().atZone(ZoneId.systemDefault()).toInstant());
            Event event = new Event(user.getName(), oneUserPreview ? String.valueOf(workLength) : user.getFullName() + " (" + workLength + "MD)", vacationDate, color).display("block");
            events.add(event);
        }

        return events;
    }

}
