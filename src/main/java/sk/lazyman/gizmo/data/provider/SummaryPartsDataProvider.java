package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sk.lazyman.gizmo.data.Part;
import sk.lazyman.gizmo.data.QAbstractTask;
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

    private static final Logger LOG = LoggerFactory.getLogger(SummaryPartsDataProvider.class);

    private PageTemplate page;

    public SummaryPartsDataProvider(PageTemplate page) {
        this.page = page;
    }

    //todo improve, this doesn't show Log entries summary
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
        query.groupBy(work.part.id);

        List<Tuple> tuples = query.list(work.part.id,
                task.workLength.sum(), work.invoiceLength.sum());
        if (tuples != null) {
            LOG.debug("Found {} parts for summary.", tuples.size());

            List<Integer> ids = new ArrayList<>();
            for (Tuple tuple : tuples) {
                Integer id = tuple.get(0, Integer.class);
                if (id != null) {
                    ids.add(id);
                }
            }
            LOG.debug("Id list size {}", ids.size());
            Map<Integer, Part> map = getProjectParts(ids);

            for (Tuple tuple : tuples) {
                Integer id = tuple.get(0, Integer.class);
                if (id == null) {
                    continue;
                }
                Part part = map.get(id);
                String name = part != null ? GizmoUtils.describeProjectPart(part, " - ") : null;
                PartSummary summary = new PartSummary(name, tuple.get(1, Double.class), tuple.get(2, Double.class));
                result.add(summary);
            }
        }

        Collections.sort(result);

        return result;
    }

    private Map<Integer, Part> getProjectParts(List<Integer> ids) {
        Map<Integer, Part> map = new HashMap<>();
        if (ids.isEmpty()) {
            return map;
        }

        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.from(QPart.part);
        query.where(QPart.part.id.in(ids));

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
