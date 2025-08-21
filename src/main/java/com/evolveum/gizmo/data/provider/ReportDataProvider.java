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
import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.evolveum.gizmo.dto.WorkDto;
import com.evolveum.gizmo.dto.WorkType;
import com.evolveum.gizmo.web.PageTemplate;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.ExpressionUtils;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.JPAExpressions;
import com.querydsl.jpa.impl.JPAQuery;
import jakarta.persistence.EntityManager;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class ReportDataProvider extends SortableDataProvider<WorkDto, String> {

    private PageTemplate page;
    private ReportFilterDto filter;

    public ReportDataProvider(PageTemplate page) {
        this.page = page;
    }

    @Override
    public Iterator<WorkDto> iterator(long first, long count) {


        QAbstractTask task = QAbstractTask.abstractTask;

        JPAQuery<?> query = query(task, page.getEntityManager(), filter);
        query.orderBy(task.date.desc());

        query.offset(first);
        query.limit(count);

        List<AbstractTask> found = query.select(task).fetch();
        if (found != null) {
            return convertToDto(found);
        }

        return Collections.emptyIterator();
    }

    private Iterator<WorkDto> convertToDto(List<AbstractTask> task) {
        return task.stream().map(t -> new WorkDto((Work) t)).iterator();
    }

    public static JPAQuery<?> query(QAbstractTask task, EntityManager entityManager, ReportFilterDto filter) {
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        QWork work = task.as(QWork.class);
        query.from(QAbstractTask.abstractTask).leftJoin(work.part.project);
        query.where(createPredicates(filter));
        return query;
    }

    @Override
    public long size() {
        QAbstractTask task = QAbstractTask.abstractTask;
        JPAQuery<?> query = query(task, page.getEntityManager(), filter);
//        JPAQuery query = new JPAQuery(page.getEntityManager());
//        QAbstractTask task = QAbstractTask.abstractTask;
//        QWork work = task.as(QWork.class);
//        query.from(QAbstractTask.abstractTask).leftJoin(work.part.project);
//        query.where(createPredicates(filter));

        return query.select(task).fetchCount();
    }

    @Override
    public IModel<WorkDto> model(WorkDto object) {
        return new Model<>(object);
    }

    public void setFilter(ReportFilterDto filter) {
        this.filter = filter;
    }


    public static BooleanBuilder createPredicates(ReportFilterDto filter) {
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

        p = createProjectListPredicate(filter.getCustomerProjectPartDtos(), task);
        if (p != null) {
            list.add(p);
        }


        if (filter.getLabelIds() != null && !filter.getLabelIds().isEmpty()) {
            list.add(task.type.eq(WorkType.WORK.getType()));
            QWork w = QWork.work;
            list.add(
                    JPAExpressions.selectOne()
                            .from(w)
                            .where(
                                    w.id.eq(task.id)
                                            .and(w.part.isNotNull())
                                            .and(w.part.labels.any().id.in(filter.getLabelIds()))
                            )
                            .exists()
            );
        }
        BooleanBuilder date = new BooleanBuilder();
        if (filter.getDateFrom() != null) {
            date.and(task.date.goe(filter.getDateFrom()));
        }
        if (filter.getDateTo()   != null) {
            date.and(task.date.loe(filter.getDateTo()));
        }
        if (date.getValue() != null) {
            list.add(date);
        }

        if (list.isEmpty()) {
            return null;
        }

        BooleanBuilder where = new BooleanBuilder();
        for (Predicate pr : list) { where.and(pr); }
        return where;
    }

    public static List<Predicate> createTimeOffPredicates(ReportFilterDto filter) {
        QAbstractTask task = QAbstractTask.abstractTask;
        if (filter == null) {
            return null;
        }

        List<Predicate> list = new ArrayList<>();

        QWork work = task.as(QWork.class);
        list.add(work.part.project.off.isTrue());

        if (filter.getDateFrom() != null) {
            list.add(task.date.goe(filter.getDateFrom()));
        }

        if (filter.getDateTo() != null) {
            list.add(task.date.loe(filter.getDateTo()));
        }

        return list;
    }

    private static Predicate createProjectListPredicate(List<CustomerProjectPartDto> list, QAbstractTask task) {
        if (list == null || list.isEmpty()) {
            return null;
        }
        QWork w = QWork.work;

        BooleanBuilder anyOf = new BooleanBuilder();

        for (CustomerProjectPartDto dto : list) {
            BooleanBuilder one = new BooleanBuilder();

            if (dto.getPartId() != null) {
                one.and(w.part.isNotNull())
                        .and(w.part.id.eq(dto.getPartId()));
            }
            if (dto.getProjectId() != null) {
                one.and(w.part.isNotNull())
                        .and(w.part.project.id.eq(dto.getProjectId()));
            }
            if (dto.getCustomerId() != null) {
                one.and(w.part.isNotNull())
                        .and(w.part.project.isNotNull())
                        .and(w.part.project.customer.id.eq(dto.getCustomerId()));
            }

            if (one.hasValue()) {
                anyOf.or(one);
            }
        }
        if (!anyOf.hasValue()) {
            return null;
        }
        return ExpressionUtils.allOf(
                task.type.eq(WorkType.WORK.getType()),
                JPAExpressions.selectOne()
                        .from(w)
                        .where(
                                w.id.eq(task.id)
                                        .and(anyOf)
                        )
                        .exists()
        );
    }

    public static Predicate createPredicate(CustomerProjectPartDto dto) {
        BooleanBuilder bb = new BooleanBuilder();

        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);
        QLog log = task.as(QLog.class);

        if (dto.getPartId() != null) {
            bb.or(work.part.id.eq(dto.getPartId()));
        } else if (dto.getProjectId() != null) {
            bb.or(work.part.project.id.eq(dto.getProjectId()));
        }
        if (dto.getCustomerId() != null) {
            bb.or(log.customer.id.eq(dto.getCustomerId()));
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
