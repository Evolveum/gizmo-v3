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

import com.evolveum.gizmo.data.*;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.UserSummary;
import com.evolveum.gizmo.web.PageTemplate;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.Tuple;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.CaseBuilder;
import com.querydsl.core.types.dsl.NumberExpression;
import com.querydsl.jpa.impl.JPAQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.*;

/**
 * @author lazyman
 */
public class SummaryUserDataProvider implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SummaryUserDataProvider.class);

    private final PageTemplate page;

    public SummaryUserDataProvider(PageTemplate page) {
        this.page = page;
    }

    public List<UserSummary> createSummary(ReportFilterDto filter) {
        List<Tuple> tuples = selectSumForEmployees(filter);
        if (tuples == null) {
            return new ArrayList<>();
        }
        LOG.debug("Found {} parts for summary.", tuples.size());
        return processSummaryResults(tuples);
    }

    private NumberExpression<Double> createTimeOffCondition(ReportFilterDto filter, QWork work) {
        List<Predicate> timeOffPredicates = ReportDataProvider.createTimeOffPredicates(filter);
        BooleanBuilder timeOff = new BooleanBuilder();
        if (!timeOffPredicates.isEmpty()) {
            timeOff.orAllOf(timeOffPredicates.toArray(new Predicate[timeOffPredicates.size()]));
            timeOff.or(work.part.project.off.isTrue());
        }

        return new CaseBuilder()
                .when(timeOff)
                .then(work.workLength)
                .otherwise(0.0)
                .sum();
    }

    private NumberExpression<Double> createAllWorkCondition(BooleanBuilder allWork, QWork work) {
        return new CaseBuilder()
                .when(allWork)
                .then(work.workLength)
                .otherwise(0.0)
                .sum();
    }

    private List<Tuple> selectSumForEmployees(ReportFilterDto filter) {

        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);

        List<Predicate> list = ReportDataProvider.createPredicates(filter);
        BooleanBuilder allWork = new BooleanBuilder();
        if (!list.isEmpty()) {
            allWork.orAllOf(list.toArray(new Predicate[list.size()]));
        }

        NumberExpression<Double> timeOffCondition = createTimeOffCondition(filter, work);
        NumberExpression<Double> allWorkCondition = createAllWorkCondition(allWork, work);


        JPAQuery<Tuple> query = new JPAQuery<>(page.getEntityManager());
        query.from(task).leftJoin(work.part.project);
        query.where(allWork);
        query.groupBy(work.realizator);
        query.select(task.date.max(),
                timeOffCondition,
                work.realizator,
                allWorkCondition,
                allWorkCondition.subtract(timeOffCondition));

        return query.fetch();

    }

    /**
     * @param tuples contains columns (partId, customerId, sum(workLength), sum(invoiceLength)
     * @return summary of time spend working, time-off and all time spend
     */
    private List<UserSummary> processSummaryResults(List<Tuple> tuples) {
        List<UserSummary> result = new ArrayList<>();
        for (Tuple tuple : tuples) {
            UserSummary userSummary = new UserSummary(
                    tuple.get(2, User.class),
                    tuple.get(0, LocalDate.class),
                    tuple.get(1, double.class),
                    tuple.get(3, double.class),
                    tuple.get(4, double.class));
            result.add(userSummary);
        }

        Collections.sort(result);
        return result;
    }
}
