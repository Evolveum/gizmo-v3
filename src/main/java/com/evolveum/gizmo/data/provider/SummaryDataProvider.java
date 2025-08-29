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

package com.evolveum.gizmo.data.provider;

import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.User;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import com.evolveum.gizmo.data.QAbstractTask;
import com.evolveum.gizmo.data.QWork;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.SummaryPanelDto;
import com.evolveum.gizmo.dto.TaskLength;
import com.evolveum.gizmo.web.PageTemplate;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * @author lazyman
 */
public class SummaryDataProvider implements Serializable {

    private PageTemplate page;

    public SummaryDataProvider(PageTemplate page) {
        this.page = page;
    }

    /**
     * select date_trunc('day' ,date), sum(length), sum(invoice) from tasks
     * where realizator_id=9 and date >= '2014-08-01 00:00:00' and date <= '2014-08-31 23:59:59'
     * group by date_trunc('day' ,date) order by date_trunc('day' ,date);
     *
     * @param filter
     * @return
     */
    public List<SummaryPanelDto> createSummary(ReportFilterDto filter) {
        QAbstractTask task = QAbstractTask.abstractTask;


        JPAQuery<?> query = ReportDataProvider.query(task, page.getEntityManager(), filter);

        List<SummaryPanelDto> dtos = new ArrayList<>();

        QWork work = task.as(QWork.class);
        query.groupBy(createDateTruncExpression(work), task.realizator);
        query.select(task.realizator, createDateTruncExpression(work), task.workLength.sum(), work.invoiceLength.sum());

        List<Tuple> tuples = (List<Tuple>) query.fetch();
        if (tuples != null) {
            for (Tuple tuple : tuples) {
                TaskLength taskLength = new TaskLength(tuple.get(2, Double.class), tuple.get(3, Double.class));
                LocalDate date = tuple.get(1, LocalDate.class);
                User realizator = tuple.get(0, User.class);
                SummaryPanelDto dto = new SummaryPanelDto(realizator, date, taskLength);
                dtos.add(dto);

            }
        }

        return dtos;
    }

    private DateTimeExpression createDateTruncExpression(QWork work) {
        return Expressions.dateTimeTemplate(LocalDate.class, "date_trunc('day',{0})", work.date);
    }
}
