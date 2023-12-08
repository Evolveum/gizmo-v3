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

package sk.lazyman.gizmo.data.provider;

import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.TemplateExpressionImpl;
import com.querydsl.core.types.dsl.DateTimeExpression;
import com.querydsl.core.types.dsl.DateTimeTemplate;
import com.querydsl.core.types.dsl.Expressions;
import com.querydsl.jpa.impl.JPAQuery;
import sk.lazyman.gizmo.data.QAbstractTask;
import sk.lazyman.gizmo.data.QWork;
import sk.lazyman.gizmo.dto.ReportFilterDto;
import sk.lazyman.gizmo.dto.SummaryPanelDto;
import sk.lazyman.gizmo.dto.TaskLength;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.web.PageTemplate;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
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
    public SummaryPanelDto createSummary(ReportFilterDto filter) {
        SummaryPanelDto dto = new SummaryPanelDto(filter);

        List<Predicate> list = AbstractTaskDataProvider.createPredicates(filter);
        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);

        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.from(task).leftJoin(work.part.project);
        if (!list.isEmpty()) {
            BooleanBuilder bb = new BooleanBuilder();
            bb.orAllOf(list.toArray(new Predicate[list.size()]));
            query.where(bb);
        }
        query.groupBy(createDateTruncExpression(work));
        query.select(createDateTruncExpression(work), task.workLength.sum(), work.invoiceLength.sum());

        List<Tuple> tuples = query.fetch();
//        List<Tuple> tuples = query.list(createDateTruncExpression(work),
//                task.workLength.sum(), work.invoiceLength.sum());
        if (tuples != null) {
            for (Tuple tuple : tuples) {
                TaskLength taskLength = new TaskLength(tuple.get(1, Double.class), tuple.get(2, Double.class));
                LocalDate date = tuple.get(0, LocalDate.class);
//                LocalDate date = day.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                dto.getDates().put(date, taskLength);
            }
        }

        return dto;
    }

    private DateTimeExpression createDateTruncExpression(QWork work) {
        return Expressions.dateTimeTemplate(LocalDate.class, "date_trunc('day',{0})", work.date);
    }
}
