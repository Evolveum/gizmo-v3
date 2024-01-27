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

import com.evolveum.gizmo.dto.CustomerProjectPartDto;
import com.evolveum.gizmo.dto.ReportFilterDto;
import com.querydsl.core.BooleanBuilder;
import com.querydsl.core.types.Predicate;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.EntityPathBase;
import com.querydsl.jpa.impl.JPAQuery;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import com.evolveum.gizmo.data.AbstractTask;
import com.evolveum.gizmo.data.QAbstractTask;
import com.evolveum.gizmo.data.QLog;
import com.evolveum.gizmo.data.QWork;
import com.evolveum.gizmo.dto.WorkType;
import com.evolveum.gizmo.web.PageTemplate;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class ReportDataProvider extends SortableDataProvider<AbstractTask, String> {

    private PageTemplate page;
    private ReportFilterDto filter;

    public ReportDataProvider(PageTemplate page) {
        this.page = page;
    }

    @Override
    public Iterator<? extends AbstractTask> iterator(long first, long count) {
        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);

        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.from(QAbstractTask.abstractTask).leftJoin(work.part.project);
        query.where(createPredicate());
        query.orderBy(task.date.asc());

        query.offset(first);
        query.limit(count);

        List<AbstractTask> found = query.select(task).fetch();
        if (found != null) {
            return found.iterator();
        }

        return new ArrayList<AbstractTask>().iterator();
    }

    @Override
    public long size() {
        JPAQuery query = new JPAQuery(page.getEntityManager());
        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);
        query.from(QAbstractTask.abstractTask).leftJoin(work.part.project);
        query.where(createPredicate());

        return query.select(task).fetchCount();
    }

    @Override
    public IModel<AbstractTask> model(AbstractTask object) {
        return new Model<>(object);
    }

    public void setFilter(ReportFilterDto filter) {
        this.filter = filter;
    }

    private Predicate createPredicate() {
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

        List<CustomerProjectPartDto> allFilter = new ArrayList<>();
        allFilter.addAll(filter.getCustomerProjectPart());
        allFilter.addAll(filter.getCustomer());
        allFilter.addAll(filter.getProject());
        p = createProjectListPredicate(allFilter);
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
