package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.Tuple;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.expr.DateTimeExpression;
import com.mysema.query.types.template.DateTimeTemplate;
import sk.lazyman.gizmo.data.QTask;
import sk.lazyman.gizmo.dto.SummaryPanelDto;
import sk.lazyman.gizmo.dto.TaskFilterDto;
import sk.lazyman.gizmo.dto.TaskLength;
import sk.lazyman.gizmo.web.PageTemplate;

import java.io.Serializable;
import java.util.ArrayList;
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
    public SummaryPanelDto createSummary(TaskFilterDto filter) {
        SummaryPanelDto dto = new SummaryPanelDto(filter);

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
        query.groupBy(createDateTruncExpression());

        List<Tuple> tuples = query.list(createDateTruncExpression(),
                QTask.task.taskLength.sum(), QTask.task.invoiceLength.sum());
        if (tuples != null) {
            for (Tuple tuple : tuples) {
                TaskLength task = new TaskLength(tuple.get(1, Double.class), tuple.get(1, Double.class));
                dto.getDates().put(tuple.get(0, Date.class), task);
            }
        }

        return dto;
    }

    private DateTimeExpression createDateTruncExpression() {
        return DateTimeTemplate.create(Date.class, "date_trunc('day',{0})", QTask.task.date);
    }
}
