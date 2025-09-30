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
import org.springframework.security.core.parameters.P;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class LabelsDataProvider extends SortableDataProvider<CustomerProjectPartDto, String> {

    private PageTemplate page;
    private ReportFilterDto filter;

    public LabelsDataProvider(PageTemplate page) {
        this.page = page;
    }

    @Override
    public Iterator<CustomerProjectPartDto> iterator(long first, long count) {
        return selectCustomerProjectPartDtos(first, count, page, filter).iterator();
    }


    public static List<CustomerProjectPartDto> selectCustomerProjectPartDtos(Long first, Long count, PageTemplate page, ReportFilterDto filter) {
        QPart part = QPart.part;

        JPAQuery<?> query = query(part, page.getEntityManager(), filter);
        query.orderBy(part.project.customer.name.desc());

        if (first != null) {
            query.offset(first);
        }
        if (count != null) {
            query.limit(count);
        }


        List<Part> found = query.select(part).fetch();
        if (found != null) {
            return convertToDto(found);
        }

        return Collections.emptyList();
    }
    private static List<CustomerProjectPartDto> convertToDto(List<Part> task) {
        return task.stream()
                .map(LabelsDataProvider::createCustomerProjectPartDto)
                .toList();
    }

    private static  CustomerProjectPartDto createCustomerProjectPartDto(Part part) {
        if (part == null) {
            return null;
        }
        Project project = part.getProject();
        Customer customer = project.getCustomer();
        return new CustomerProjectPartDto(
                customer.getName(), project.getName(), part.getName(),
                customer.getId(), project.getId(), part.getId());

    }

    public static JPAQuery<?> query(QPart part, EntityManager entityManager, ReportFilterDto filter) {
        JPAQuery<?> query = new JPAQuery<>(entityManager);
        QLabelPart labelPart = QLabelPart.labelPart;
        query.from(QPart.part).leftJoin(part.project.customer).join(part.labels, labelPart);
        query.where(createPredicates(filter, labelPart));
        return query;
    }

    @Override
    public long size() {
        QPart part = QPart.part;
        JPAQuery<?> query = query(part, page.getEntityManager(), filter);
//        JPAQuery query = new JPAQuery(page.getEntityManager());
//        QAbstractTask task = QAbstractTask.abstractTask;
//        QWork work = task.as(QWork.class);
//        query.from(QAbstractTask.abstractTask).leftJoin(work.part.project);
//        query.where(createPredicates(filter));

        return query.select(part).fetchCount();
    }

    @Override
    public IModel<CustomerProjectPartDto> model(CustomerProjectPartDto object) {
        return new Model<>(object);
    }

    public void setFilter(ReportFilterDto filter) {
        this.filter = filter;
    }


    public static BooleanBuilder createPredicates(ReportFilterDto filter, QLabelPart labelPart) {
        List<Predicate> list = new ArrayList<>();

        List<LabelPart> labelParts = filter.getLabels();
        Predicate p = createLabelsPredicate(labelParts, labelPart);
        if (p != null) {
            list.add(p);
        }

        BooleanBuilder allParts = new BooleanBuilder();

        if (list.isEmpty()) {
            return null;
        }

        allParts.orAllOf(list.toArray(new Predicate[0]));
        return allParts;
    }


    public static Predicate createLabelsPredicate(List<LabelPart> labels, QLabelPart labelPart) {
        BooleanBuilder bb = new BooleanBuilder();

//        QAbstractTask task = QAbstractTask.abstractTask;

//        QWork work = task.as(QWork.class);
        if (!labels.isEmpty()) {
            bb.or(labelPart.in(labels));
//            bb.or(work.part.labels.any().in(labels));
        }
        return bb;
    }
}
