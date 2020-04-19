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
import com.querydsl.jpa.impl.JPAQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.lazyman.gizmo.data.*;
import sk.lazyman.gizmo.dto.PartSummary;
import sk.lazyman.gizmo.dto.WorkFilterDto;
import sk.lazyman.gizmo.util.GizmoUtils;
import sk.lazyman.gizmo.web.PageTemplate;

import java.io.Serializable;
import java.util.*;

/**
 * @author lazyman
 */
public class SummaryPartsDataProvider implements Serializable {

    private static final Logger LOG = LoggerFactory.getLogger(SummaryPartsDataProvider.class);

    private PageTemplate page;

    public SummaryPartsDataProvider(PageTemplate page) {
        this.page = page;
    }

    public List<PartSummary> createSummary(WorkFilterDto filter) {
        List<Predicate> list = AbstractTaskDataProvider.createPredicates(filter);
        QAbstractTask task = QAbstractTask.abstractTask;
        QWork work = task.as(QWork.class);
        QLog log = task.as(QLog.class);

        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.from(task).leftJoin(work.part.project);
        if (!list.isEmpty()) {
            BooleanBuilder bb = new BooleanBuilder();
            bb.orAllOf(list.toArray(new Predicate[list.size()]));
            query.where(bb);
        }
        query.groupBy(work.part.id, log.customer.id);
        query.select(work.part.id, log.customer.id, task.workLength.sum(), work.invoiceLength.sum());

        List<Tuple> tuples = query.fetch();
        List<PartSummary> result = new ArrayList<>();
        if (tuples != null) {
            LOG.debug("Found {} parts for summary.", tuples.size());

            processSummaryResults(tuples, result);
        }

        Collections.sort(result);

        return result;
    }

    /**
     * @param tuples contains columns (partId, customerId, sum(workLength), sum(invoiceLength)
     * @return
     */
    private List<PartSummary> processSummaryResults(List<Tuple> tuples, List<PartSummary> result) {
        List<Integer> partIds = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Integer id = tuple.get(0, Integer.class);
            if (id != null) {
                //part id
                partIds.add(id);
            } else {
                Integer custId = tuple.get(1, Integer.class);
                if (custId != null) {
                    customerIds.add(custId);
                }
            }
        }
        LOG.debug("Id list size {}", partIds.size());
        Map<Integer, Part> map = getProjectParts(partIds);
        Map<Integer, Customer> customerMap = getCustomers(customerIds);

        for (Tuple tuple : tuples) {
            Integer id = tuple.get(0, Integer.class);
            if (id != null) {
                //part id
                Part part = map.get(id);
                String name = part != null ? GizmoUtils.describeProjectPart(part, " - ") : null;
                PartSummary summary = new PartSummary(name, tuple.get(2, Double.class), tuple.get(3, Double.class));
                result.add(summary);
            } else {
                //customer id
                id = tuple.get(1, Integer.class);
                Customer customer = customerMap.get(id);
                String name = customer != null ? customer.getName() : null;
                PartSummary summary = new PartSummary(name, tuple.get(2, Double.class), tuple.get(3, Double.class));
                result.add(summary);
            }

        }

        return result;
    }

    private Map<Integer, Customer> getCustomers(List<Integer> ids) {
        Map<Integer, Customer> map = new HashMap<>();
        if (ids.isEmpty()) {
            return map;
        }

        QCustomer customer = QCustomer.customer;
        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.select(customer)
                .from(customer)
                .where(customer.id.in(ids));

        List<Customer> customers = query.fetch();
        if (customers == null) {
            return map;
        }

        for (Customer c : customers) {
            map.put(c.getId(), c);
        }

        return map;
    }

    private Map<Integer, Part> getProjectParts(List<Integer> ids) {
        Map<Integer, Part> map = new HashMap<>();
        if (ids.isEmpty()) {
            return map;
        }

        QPart part = QPart.part;
        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.select(part)
                .from(part)
                .where(part.id.in(ids));

        List<Part> parts = query.fetch();
        if (parts == null) {
            return map;
        }

        for (Part p : parts) {
            map.put(p.getId(), p);
        }

        return map;
    }
}
