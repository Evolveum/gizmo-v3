package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Predicate;
import sk.lazyman.gizmo.data.ProjectPart;
import sk.lazyman.gizmo.data.QProjectPart;
import sk.lazyman.gizmo.data.QTask;
import sk.lazyman.gizmo.dto.PartSummary;
import sk.lazyman.gizmo.dto.TaskFilterDto;
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
    public List<PartSummary> createSummary(TaskFilterDto filter) {
        List<PartSummary> result = new ArrayList<>();

        List<Predicate> list = new ArrayList<>();
        if (filter.getRealizator() != null) {
            list.add(QTask.task.realizator.eq(filter.getRealizator()));
        }
        if (filter.getFrom() != null) {
            list.add(QTask.task.date.goe(filter.getFrom()));
        }
        if (filter.getTo() != null) {
            list.add(QTask.task.date.loe(filter.getTo()));
        }

        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.from(QTask.task);
        if (!list.isEmpty()) {
            BooleanBuilder bb = new BooleanBuilder();
            bb.orAllOf(list.toArray(new Predicate[list.size()]));
            query.where(bb);
        }
        query.groupBy(QTask.task.projectPart.id);

        List<Tuple> tuples = query.list(QTask.task.projectPart.id,
                QTask.task.taskLength.sum(), QTask.task.invoiceLength.sum());
        if (tuples != null) {
            List<Integer> ids = new ArrayList<>();
            for (Tuple tuple : tuples) {
                ids.add(tuple.get(0, Integer.class));
            }
            Map<Integer, ProjectPart> map = getProjectParts(ids);

            for (Tuple tuple : tuples) {
                ProjectPart part = map.get(tuple.get(0, Integer.class));
                String name = part != null ? GizmoUtils.describeProjectPart(part, " - ") : null;
                PartSummary summary = new PartSummary(name, tuple.get(1, Double.class), tuple.get(2, Double.class));
                result.add(summary);
            }
        }

        Collections.sort(result);

        return result;
    }

    private Map<Integer, ProjectPart> getProjectParts(List<Integer> ids) {
        JPAQuery query = new JPAQuery(page.getEntityManager());
        query.from(QProjectPart.projectPart);
        query.where(QProjectPart.projectPart.id.in(ids));

        Map<Integer, ProjectPart> map = new HashMap<>();

        List<ProjectPart> parts = query.list(QProjectPart.projectPart);
        if (parts == null) {
            return map;
        }

        for (ProjectPart part: parts) {
            map.put(part.getId(), part);
        }

        return map;
    }
}
