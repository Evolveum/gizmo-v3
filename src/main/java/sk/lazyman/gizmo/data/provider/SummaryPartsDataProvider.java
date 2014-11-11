package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Predicate;
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

        List<Tuple> tuples = query.list(work.part.id, log.customer.id,
                task.workLength.sum(), work.invoiceLength.sum());
        List<PartSummary> result = new ArrayList<>();
        if (tuples != null) {
            LOG.debug("Found {} parts for summary.", tuples.size());

            processSummaryResults(tuples);
        }

        Collections.sort(result);

        return result;
    }

    /**
     * @param tuples contains columns (partId, customerId, sum(workLength), sum(invoiceLength)
     * @return
     */
    private List<PartSummary> processSummaryResults(List<Tuple> tuples) {
        List<Integer> partIds = new ArrayList<>();
        List<Integer> customerIds = new ArrayList<>();
        for (Tuple tuple : tuples) {
            Integer id = tuple.get(0, Integer.class);
            if (id != null) {
                //part id
                partIds.add(id);
            } else {
                customerIds.add(tuple.get(1, Integer.class));
            }
        }
        LOG.debug("Id list size {}", partIds.size());
        Map<Integer, Part> map = getProjectParts(partIds);
        Map<Integer, Customer> customerMap = getCustomers(customerIds);

        List<PartSummary> result = new ArrayList<>();
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
        query.from(customer);
        query.where(customer.id.in(ids));

        List<Customer> customers = query.list(customer);
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
        query.from(part);
        query.where(part.id.in(ids));

        List<Part> parts = query.list(part);
        if (parts == null) {
            return map;
        }

        for (Part p : parts) {
            map.put(p.getId(), p);
        }

        return map;
    }
}
