package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Predicate;
import sk.lazyman.gizmo.data.Part;
import sk.lazyman.gizmo.data.QPart;
import sk.lazyman.gizmo.data.QWork;
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

    private PageTemplate page;

    public SummaryPartsDataProvider(PageTemplate page) {
        this.page = page;
    }

    /**
     * select part_id, sum(length), sum(invoice) from tasks
     * where realizator_id=9 and date >= '2014-08-01 00:00:00' and date <= '2014-08-31 23:59:59'
     * group by part_id;
     *
     * @param filter
     * @return
     */
    public List<PartSummary> createSummary(WorkFilterDto filter) {
        List<PartSummary> result = new ArrayList<>();

        List<Predicate> list = new ArrayList<>();
        if (filter.getRealizator() != null) {
            list.add(QWork.work.realizator.eq(filter.getRealizator()));
        }
        if (filter.getFrom() != null) {
            list.add(QWork.work.date.goe(filter.getFrom()));
        }
        if (filter.getTo() != null) {
            list.add(QWork.work.date.loe(filter.getTo()));
        }

        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.from(QWork.work);
        if (!list.isEmpty()) {
            BooleanBuilder bb = new BooleanBuilder();
            bb.orAllOf(list.toArray(new Predicate[list.size()]));
            query.where(bb);
        }
        query.groupBy(QWork.work.part.id);

        List<Tuple> tuples = query.list(QWork.work.part.id,
                QWork.work.workLength.sum(), QWork.work.invoiceLength.sum());
        if (tuples != null) {
            List<Integer> ids = new ArrayList<>();
            for (Tuple tuple : tuples) {
                ids.add(tuple.get(0, Integer.class));
            }
            Map<Integer, Part> map = getProjectParts(ids);

            for (Tuple tuple : tuples) {
                Part part = map.get(tuple.get(0, Integer.class));
                String name = part != null ? GizmoUtils.describeProjectPart(part, " - ") : null;
                PartSummary summary = new PartSummary(name, tuple.get(1, Double.class), tuple.get(2, Double.class));
                result.add(summary);
            }
        }

        Collections.sort(result);

        return result;
    }

    private Map<Integer, Part> getProjectParts(List<Integer> ids) {
        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.from(QPart.part);
        query.where(QPart.part.id.in(ids));

        Map<Integer, Part> map = new HashMap<>();

        List<Part> parts = query.list(QPart.part);
        if (parts == null) {
            return map;
        }

        for (Part part : parts) {
            map.put(part.getId(), part);
        }

        return map;
    }
}
