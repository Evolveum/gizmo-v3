package sk.lazyman.gizmo.data.provider;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.Predicate;
import com.mysema.query.types.PredicateOperation;
import com.mysema.query.types.expr.BooleanExpression;
import org.apache.wicket.extensions.markup.html.repeater.util.SortableDataProvider;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import sk.lazyman.gizmo.data.EmailLog;
import sk.lazyman.gizmo.data.QEmailLog;
import sk.lazyman.gizmo.dto.EmailFilterDto;
import sk.lazyman.gizmo.repository.EmailLogRepository;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @author lazyman
 */
public class EmailDataProvider extends SortableDataProvider<EmailLog, String> {

    private EmailLogRepository emailRepository;
    private EmailFilterDto filter;

    public EmailDataProvider(EmailLogRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    @Override
    public Iterator<? extends EmailLog> iterator(long first, long count) {
        Sort sort = new Sort(Sort.Direction.DESC, EmailLog.F_DATE);

        PageRequest page = new PageRequest((int) first, (int) count, sort);
        Page<EmailLog> found = emailRepository.findAll(createPredicate(), page);
        if (found != null) {
            return found.iterator();
        }

        return new ArrayList<EmailLog>().iterator();
    }

    @Override
    public long size() {
        return emailRepository.count();
    }

    @Override
    public IModel<EmailLog> model(EmailLog object) {
        return new Model<>(object);
    }

    public void setFilter(EmailFilterDto filter) {
        this.filter = filter;
    }

    private Predicate createPredicate() {
        if (filter == null
                || (filter.getFrom() == null && filter.getTo() == null && filter.getSender() == null)) {
            return null;
        }

        List<Predicate> list = new ArrayList<>();
        if (filter.getFrom() != null) {
            list.add(QEmailLog.emailLog.date.goe(filter.getFrom()));
        }

        if (filter.getTo() != null) {
            list.add(QEmailLog.emailLog.date.loe(filter.getTo()));
        }

        if (filter.getSender() != null) {
            list.add(QEmailLog.emailLog.sender.eq(filter.getSender()));
        }

        BooleanBuilder bb = new BooleanBuilder();
        return bb.orAllOf(list.toArray(new Predicate[list.size()]));
    }
}
